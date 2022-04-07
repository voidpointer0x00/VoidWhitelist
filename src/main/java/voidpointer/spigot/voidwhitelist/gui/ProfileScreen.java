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
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.util.Collections;
import java.util.Optional;

@Getter
final class ProfileScreen extends AbstractGui {
    private final WhitelistGui parent;
    private final ProfileSkull profileSkull;
    @Setter private StaticPane profilePane;
    @Setter private GuiItem removeButton;
    @Setter private GuiItem requestInfoButton;
    @Setter private GuiItem editButton;
    private GuiItem infoButton;

    ProfileScreen(final WhitelistGui parent, final ProfileSkull profileSkull) {
        super(new ChestGui(4, "§6" + (profileSkull.getProfile().getName() != null
                ? profileSkull.getProfile().getName()
                : profileSkull.getProfile().getUuid().toString())));
        this.parent = parent;
        this.profileSkull = profileSkull;
        addPane(GuiPanes.createProfilePane(this));
    }

    public void back(final InventoryClickEvent event) {
        parent.show(event.getWhoClicked());
    }

    public void onRemoveButtonClick(final InventoryClickEvent event) {
        final Profile profile = profileSkull.getProfile();
        getWhitelistService().find(profile.getUuid())
                .exceptionally(this::onRemoveFindException)
                .thenAcceptAsync(whitelistableOptional -> {
                    if (!whitelistableOptional.isPresent()) {
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
        // TODO: add WhitelistGui::refresh() method - public version of fillCurrentPage
    }

    private void onNotRemoved() {
        ItemMeta removeButtonMeta = removeButton.getItem().getItemMeta();
        if (removeButtonMeta != null) {
            removeButtonMeta.setDisplayName("§cRemove operation failed");
            removeButtonMeta.setLore(Collections.singletonList("§6Check console log info, if you have access"));
        }
        removeButton.getItem().setItemMeta(removeButtonMeta);
        update();
    }

    public void onRequestInfoButtonClick(final InventoryClickEvent event) {
        getWhitelistService().find(profileSkull.getProfile().getUuid())
                .exceptionally(this::onRequestInfoFindException)
                .thenAcceptAsync(whitelistable -> {
                    if (whitelistable.isPresent()) /* TODO: Java 1.9 #ifPresetOrElse() */
                        displayInfo(whitelistable.get());
                    else
                        infoNotFound();
                }).exceptionally(this::onDisplayInfoException);
        update();
    }

    public void onEditButtonClick(final InventoryClickEvent event) {

    }

    private void displayInfo(final Whitelistable whitelistable) {
        profilePane.addItem(GuiPanes.createInfoButton(whitelistable), 4, 2);
    }

    private void infoNotFound() {
        ItemMeta meta = requestInfoButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for requestInfoButton cannot be null";
        meta.setDisplayName("§cNothing was found.");
        requestInfoButton.getItem().setItemMeta(meta);
    }

    private Void onDisplayInfoException(final Throwable thrown) {
        ItemMeta meta = requestInfoButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for requestInfoButton cannot be null";
        meta.setDisplayName("§cInternal error :(");
        requestInfoButton.getItem().setItemMeta(meta);
        getLocaleLog().warn("Unable to display info", thrown);
        return null;
    }

    private Optional<Whitelistable> onRequestInfoFindException(final Throwable thrown) {
        ItemMeta meta = requestInfoButton.getItem().getItemMeta();
        assert meta != null : "ItemMeta for requestInfoButton cannot be null";
        meta.setDisplayName("§cCannot find info");
        meta.setLore(Collections.singletonList("§cCheck console log for more details"));
        requestInfoButton.getItem().setItemMeta(meta);
        getLocaleLog().info("Failed searching for whitelistable on find info button", thrown);
        return Optional.empty();
    }

    private void onRemoveNotFound() {
        ItemMeta removeButtonMeta = removeButton.getItem().getItemMeta();
        if (removeButtonMeta != null)
            removeButtonMeta.setDisplayName("§cPlayer not found");
        removeButton.getItem().setItemMeta(removeButtonMeta);
        update();
    }

    private Optional<Whitelistable> onRemoveFindException(final Throwable throwable) {
        onNotRemoved();
        return Optional.empty();
    }

    private Void onRemoveException(final Throwable throwable) {
        getLocaleLog().warn("Couldn't remove "+profileSkull.getProfile()+" from the whitelist", throwable);
        onNotRemoved();
        return null;
    }
}
