/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.message.GuiMessage;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.util.Date;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static voidpointer.spigot.voidwhitelist.Whitelistable.NEVER_EXPIRES;
import static voidpointer.spigot.voidwhitelist.date.EssentialsDateParser.WRONG_DATE_FORMAT;
import static voidpointer.spigot.voidwhitelist.date.EssentialsDateParser.parseDate;
import static voidpointer.spigot.voidwhitelist.message.GuiMessage.*;

@Getter
final class ProfileScreen extends AbstractGui {
    @Autowired private static LocaleLog locale;
    private final WhitelistGui parent;
    private final ProfileSkull profileSkull;
    @Setter private StaticPane profilePane;
    @Setter private GuiItem removeButton;
    @Setter private GuiItem requestInfoButton;
    @Setter private GuiItem editButton;

    ProfileScreen(final WhitelistGui parent, final ProfileSkull profileSkull) {
        super(new ChestGui(4, getTitleFor(profileSkull)));
        this.parent = parent;
        this.profileSkull = profileSkull;
        addPane(GuiPanes.createProfilePane(this));
    }

    private static String getTitleFor(final ProfileSkull profileSkull) {
        final String nameOrUuid = profileSkull.getProfile().getName() != null
                ? profileSkull.getProfile().getName()
                : profileSkull.getProfile().getUuid().toString();
        return locale.localize(GuiMessage.PROFILE_TITLE)
                .set("player", nameOrUuid)
                .getRawMessage();
    }

    public void back(final InventoryClickEvent event) {
        parent.show(event.getWhoClicked());
    }

    public void onRemoveButtonClick(final InventoryClickEvent event) {
        final Profile profile = profileSkull.getProfile();
        getWhitelistService().find(profile.getUuid())
                .exceptionally(this::onRemoveFindException)
                .thenAcceptAsync(whitelistableOptional -> {
                    if (whitelistableOptional.isEmpty()) {
                        onRemoveNotFound();
                        return;
                    }
                    getWhitelistService().remove(whitelistableOptional.get())
                            .thenAccept(isRemoved -> {
                                if (isRemoved)
                                    onRemoved(whitelistableOptional.get());
                                else
                                    onNotRemoved();
                            })
                            .exceptionally(this::onRemoveException);
                });
    }

    private void onRemoved(final Whitelistable whitelistable) {
        getEventManager().callEvent(new WhitelistRemovedEvent(whitelistable));
        parent.removeProfile(profileSkull);
        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> getViewer().ifPresent(parent::show));
    }

    private void onRemoveNotFound() {
        ItemMeta removeButtonMeta = removeButton.getItem().getItemMeta();
        assert removeButtonMeta != null : "ItemMeta for remove button cannot be null";
        removeButtonMeta.setLore(singletonList(locale.localize(PROFILE_NOT_FOUND).getRawMessage()));
        removeButton.getItem().setItemMeta(removeButtonMeta);
        update();
    }

    private Optional<Whitelistable> onRemoveFindException(final Throwable throwable) {
        getLocaleLog().warn("Couldn't remove "+profileSkull.getProfile()+" from the whitelist", throwable);
        onNotRemoved();
        return Optional.empty();
    }

    private Void onRemoveException(final Throwable thrown) {
        onRemoveFindException(thrown);
        return null;
    }

    private void onNotRemoved() {
        ItemMeta removeButtonMeta = removeButton.getItem().getItemMeta();
        assert removeButtonMeta != null : "ItemMeta for remove button cannot be null";
        removeButtonMeta.setLore(singletonList(locale.localize(PROFILE_REMOVE_FAIL).getRawMessage()));
        removeButton.getItem().setItemMeta(removeButtonMeta);
        update();
    }

    public void onRequestInfoButtonClick(final InventoryClickEvent event) {
        getWhitelistService().find(profileSkull.getProfile().getUuid())
                .exceptionally(this::onRequestInfoFindException)
                .thenAcceptAsync(whitelistable -> whitelistable.ifPresentOrElse(this::displayInfo, this::infoNotFound))
                .exceptionally(this::onDisplayInfoException);
        update();
    }

    public void onEditButtonClick(final InventoryClickEvent event) {
        if (getViewer().isEmpty()) {
            getLocaleLog().warn("#onEditButtonClick() without viewer");
            return; /* should not be the case, but shit happens */
        }
        InputGui.ask(locale.localize(ANVIL_EDIT_TITLE).getRawMessage(), getViewer().get(),
                GuiPanes::setupEditDateAnvil, this::testInputDate, this::onEdited);
    }

    private void onEdited(final String newDate, final HumanEntity human) {
        show(human);
        getWhitelistService().find(profileSkull.getProfile().getUuid())
                .exceptionally(this::onEditFindException)
                .thenAcceptAsync(whitelistable -> {
                    if (whitelistable.isEmpty()) {
                        notifyEditNotFound();
                        return;
                    }
                    getWhitelistService().add(
                            profileSkull.getProfile().getUuid(),
                            profileSkull.getProfile().getName(),
                            isNever(newDate) ? NEVER_EXPIRES : new Date(parseDate(newDate))
                    ).whenComplete((result, thrown) -> {
                        if (thrown == null)
                            notifyEdited();
                        else
                            onEditException(thrown);
                    });
                }).exceptionally(this::onDisplayEditedException);
        update();
    }

    private void notifyEdited() {
        ItemMeta meta = editButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for edit button cannot be null";
        meta.setLore(singletonList(locale.localize(PROFILE_LORE_EDITED).getRawMessage()));
        editButton.getItem().setItemMeta(meta);
    }

    private void notifyEditNotFound() {
        ItemMeta meta = editButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for edit button cannot be null";
        meta.setLore(singletonList(locale.localize(PROFILE_NOT_FOUND).getRawMessage()));
        editButton.getItem().setItemMeta(meta);
    }

    private void onEditException(final Throwable thrown) {
        getLocaleLog().warn("Unable to edit", thrown);
        displayEditInternalException();
    }

    private Void onDisplayEditedException(final Throwable thrown) {
        getLocaleLog().warn("Couldn't display edited message", thrown);
        displayEditInternalException();
        return null;
    }

    private Optional<Whitelistable> onEditFindException(final Throwable thrown) {
        getLocaleLog().warn("Couldn't find requested whitelistable to edit", thrown);
        displayEditInternalException();
        return Optional.empty();
    }

    private void displayEditInternalException() {
        ItemMeta meta = editButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for edit button cannot be null";
        meta.setLore(singletonList(locale.localize(PROFILE_INTERNAL).getRawMessage()));
        editButton.getItem().setItemMeta(meta);
    }

    private boolean testInputDate(final AnvilGui anvilGui) {
        long expiresAtTimestamp = parseDate(anvilGui.getRenameText());
        ItemStack result = anvilGui.getResultComponent().getItem(0, 0);
        assert result != null : "Resulting item cannot be null";
        ItemMeta meta = result.getItemMeta();
        assert meta != null : "Resulting item meta cannot be null";
        boolean isValid = (expiresAtTimestamp != WRONG_DATE_FORMAT) || isNever(anvilGui.getRenameText());
        if (isValid)
            meta.setLore(singletonList(locale.localize(ANVIL_EDIT_DATE_VALID).getRawMessage()));
        else
            meta.setLore(singletonList(locale.localize(ANVIL_EDIT_DATE_INVALID).getRawMessage()));
        result.setItemMeta(meta);
        return isValid;
    }

    private boolean isNever(final String renameText) {
        return renameText.equalsIgnoreCase(locale.localize(NEVER).getRawMessage());
    }

    private void displayInfo(final Whitelistable whitelistable) {
        GuiItem infoButton = GuiPanes.createInfoButton(whitelistable);
        infoButton.setAction(this::onRequestInfoButtonClick);
        profilePane.addItem(infoButton, 4, 2);
    }

    private void infoNotFound() {
        ItemMeta meta = requestInfoButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for requestInfoButton cannot be null";
        meta.setDisplayName(locale.localize(PROFILE_INFO_NOT_FOUND).getRawMessage());
        requestInfoButton.getItem().setItemMeta(meta);
    }

    private Void onDisplayInfoException(final Throwable thrown) {
        getLocaleLog().warn("Unable to display info", thrown);
        ItemMeta meta = requestInfoButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for requestInfoButton cannot be null";
        meta.setLore(singletonList(locale.localize(PROFILE_INTERNAL).getRawMessage()));
        requestInfoButton.getItem().setItemMeta(meta);
        return null;
    }

    private Optional<Whitelistable> onRequestInfoFindException(final Throwable thrown) {
        getLocaleLog().info("Failed searching for whitelistable on find info button", thrown);
        ItemMeta meta = requestInfoButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for requestInfoButton cannot be null";
        meta.setLore(singletonList(locale.localize(PROFILE_INTERNAL).getRawMessage()));
        requestInfoButton.getItem().setItemMeta(meta);
        return Optional.empty();
    }
}
