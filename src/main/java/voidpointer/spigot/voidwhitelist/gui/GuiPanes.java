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
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.Material.NAME_TAG;
import static org.bukkit.Material.WRITABLE_BOOK;

class GuiPanes {
    public static final int FORWARD_INDEX = 0;
    public static final int BACK_INDEX = 1;
    public static final int REFRESH_INDEX = 2;
    public static final int STATUS_INDEX = 3;

    @Autowired private static LocaleLog locale;
    private static StaticPane delimiter;
    private static ItemStack backgroundItem;
    @Autowired private static WhitelistConfig whitelistConfig;

    public static ItemStack getBackgroundItem() {
        if (backgroundItem == null) {
            backgroundItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = backgroundItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(" ");
                backgroundItem.setItemMeta(meta);
            }
        }
        return backgroundItem;
    }

    public static StaticPane getDelimiter() {
        if (delimiter == null) {
            delimiter = new StaticPane(7, 0, 1, 6);
            delimiter.fillWith(getBackgroundItem());
        }
        return delimiter;
    }

    public static PaginatedPane createWhitelistPagesPane() {
        PaginatedPane whitelistPane = new PaginatedPane(0, 0, 7, 6);
        whitelistPane.addPane(0, createWhitelistPagePane());
        whitelistPane.setPage(0);
        return whitelistPane;
    }

    public static OutlinePane createWhitelistPagePane() {
        return new OutlinePane(7, 6);
    }

    public static OutlinePane createControlPane(final WhitelistGui whitelistGui) {
        OutlinePane controlPane = new OutlinePane(8, 0, 1, 6);
        ProfileSkull forward = ControlSkulls.getForward();
        ProfileSkull back = ControlSkulls.getBack();
        ProfileSkull enabled = ControlSkulls.getEnabled();
        ProfileSkull disabled = ControlSkulls.getDisabled();
        ProfileSkull currentStatus = whitelistConfig.isWhitelistEnabled() ? enabled : disabled;
        ProfileSkull refresh = ControlSkulls.getRefresh();

        whitelistGui.setDisabledButton(disabled.getGuiItem());
        whitelistGui.setEnabledButton(enabled.getGuiItem());

        forward.setDisplayName("§eNext page");
        back.setDisplayName("§ePrevious page");
        enabled.setDisplayName("§aEnabled");
        disabled.setDisplayName("§cDisabled");
        refresh.setDisplayName("§eRefresh");

        enabled.onClick(whitelistGui::onStatusClick);
        disabled.onClick(whitelistGui::onStatusClick);
        back.onClick(whitelistGui::onPreviousPageClick);
        forward.onClick(whitelistGui::onNextPageClick);
        refresh.onClick(whitelistGui::onRefresh);

        controlPane.addItem(forward.getGuiItem());
        controlPane.addItem(back.getGuiItem());
        controlPane.addItem(refresh.getGuiItem());
        controlPane.addItem(currentStatus.getGuiItem());
        controlPane.flipVertically(true);

        return controlPane;
    }

    public static void setupEditDateAnvil(final AnvilGui anvilGui) {
        setDateHintItem(anvilGui);
        setDateResultingItem(anvilGui);
    }

    private static void setDateHintItem(final AnvilGui anvilGui) {
        ItemStack nameTag = new ItemStack(NAME_TAG);
        ItemMeta meta = nameTag.getItemMeta();
        assert meta != null : "ItemMeta for name tag item cannot be null";
        meta.setDisplayName("1mon");
        nameTag.setItemMeta(meta);
        StaticPane hintPane = new StaticPane(1, 1);
        anvilGui.getFirstItemComponent().addPane(hintPane);
        hintPane.addItem(new GuiItem(nameTag), 0, 0);
    }

    private static void setDateResultingItem(final AnvilGui anvilGui) {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        assert meta != null : "ItemMeta for clock item cannot be null";
        meta.setDisplayName("§eEnter new date");
        StaticPane resultingPane = new StaticPane(1, 1);
        resultingPane.addItem(new GuiItem(clock), 0, 0);
        anvilGui.getResultComponent().addPane(resultingPane);
    }

    public static StaticPane createProfilePane(final ProfileScreen profileScreen) {
        StaticPane profilePane = new StaticPane(9, 4);
        profilePane.fillWith(GuiPanes.getBackgroundItem());

        ProfileSkull back = ControlSkulls.getBack();
        GuiItem profileSkullItem = profileScreen.getProfileSkull().getGuiItem().copy();
        GuiItem removeButton = createKickButton();
        GuiItem requestInfoButton = createRequestInfoButton();
        GuiItem editButton = createEditButton();

        back.setDisplayName("§eBack to whitelist");

        back.getGuiItem().setAction(profileScreen::back);
        profileSkullItem.setAction(event -> {});
        removeButton.setAction(profileScreen::onRemoveButtonClick);
        requestInfoButton.setAction(profileScreen::onRequestInfoButtonClick);
        editButton.setAction(profileScreen::onEditButtonClick);

        profileScreen.setProfilePane(profilePane);
        profileScreen.setRemoveButton(removeButton);
        profileScreen.setRequestInfoButton(requestInfoButton);
        profileScreen.setEditButton(editButton);

        profilePane.addItem(profileSkullItem, 4, 1);
        profilePane.addItem(back.getGuiItem(), 8, 3);
        profilePane.addItem(removeButton, 5, 2);
        profilePane.addItem(requestInfoButton, 4, 2);
        profilePane.addItem(editButton, 3, 2);

        return profilePane;
    }

    private static GuiItem createEditButton() {
        ItemStack editButtonItem = new ItemStack(WRITABLE_BOOK);
        ItemMeta meta = editButtonItem.getItemMeta();
        assert meta != null : "ItemMeta for \"edit\" button item cannot be null";
        meta.setDisplayName("§eEdit expire time");
        editButtonItem.setItemMeta(meta);
        return new GuiItem(editButtonItem);
    }

    public static GuiItem createInfoButton(final Whitelistable whitelistable) {
        ItemStack infoButtonItem = new ItemStack(ENCHANTED_BOOK);
        ItemMeta meta = infoButtonItem.getItemMeta();
        assert meta != null : "ItemMeta for \"info\" button item cannot be null";
        meta.setDisplayName("§eDetails");
        List<String> lore = new ArrayList<>(2);
        lore.add("§eExpires at: "+(whitelistable.getExpiresAt()==null ?"§6never":"§d"+whitelistable.getExpiresAt()));
        boolean allowedToJoin = whitelistable.isAllowedToJoin();
        lore.add("§eAllowed to join: " + (allowedToJoin ? "§a" : "§c") + allowedToJoin);
        meta.setLore(lore);
        infoButtonItem.setItemMeta(meta);
        return new GuiItem(infoButtonItem);
    }

    private static GuiItem createRequestInfoButton() {
        ItemStack requestInfoButtonItem = new ItemStack(BOOK);
        ItemMeta meta = requestInfoButtonItem.getItemMeta();
        assert meta != null : "ItemMeta for \"requestInfo\" button item cannot be null";
        meta.setDisplayName("§eMore info");
        requestInfoButtonItem.setItemMeta(meta);
        return new GuiItem(requestInfoButtonItem);
    }

    private static GuiItem createKickButton() {
        ItemStack removeButtonItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = removeButtonItem.getItemMeta();
        assert meta != null : "ItemMeta for \"remove\" button item cannot be null";
        meta.setDisplayName("§cRemove from the whitelist");
        removeButtonItem.setItemMeta(meta);
        return new GuiItem(removeButtonItem);
    }
}
