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

import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

class GuiPanes {
    public static final int FORWARD_INDEX = 0;
    public static final int BACK_INDEX = 1;
    public static final int STATUS_INDEX = 2;

    @AutowiredLocale private static Locale locale;
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
        ProfileSkull current = whitelistConfig.isWhitelistEnabled() ? enabled : disabled;

        whitelistGui.setDisabledButton(disabled.getGuiItem());
        whitelistGui.setEnabledButton(enabled.getGuiItem());

        forward.setDisplayName("§eNext page");
        back.setDisplayName("§ePrevious page");
        enabled.setDisplayName("§aEnabled");
        disabled.setDisplayName("§cDisabled");

        enabled.onClick(whitelistGui::onStatusClick);
        disabled.onClick(whitelistGui::onStatusClick);
        back.onClick(whitelistGui::onPreviousPageClick);
        forward.onClick(whitelistGui::onNextPageClick);

        controlPane.insertItem(forward.getGuiItem(), FORWARD_INDEX);
        controlPane.insertItem(back.getGuiItem(), BACK_INDEX);
        controlPane.insertItem(current.getGuiItem(), STATUS_INDEX);
        controlPane.flipVertically(true);

        return controlPane;
    }
}
