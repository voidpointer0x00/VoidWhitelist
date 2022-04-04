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
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

class GuiPanes {
    private static StaticPane delimiter;

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    public static StaticPane getDelimiter() {
        if (delimiter == null) {
            delimiter = new StaticPane(7, 0, 1, 6);
            delimiter.fillWith(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        return delimiter;
    }

    public static PaginatedPane createWhitelistPane() {
        PaginatedPane whitelistPane = new PaginatedPane(0, 0, 7, 6);
        whitelistPane.addPane(0, new OutlinePane(7, 6));
        whitelistPane.setPage(0);
        return whitelistPane;
    }

    public static OutlinePane createControlPane(final WhitelistGui whitelistGui) {
        OutlinePane controlPane = new OutlinePane(8, 0, 1, 6);
        ProfileSkull forward = ControlSkulls.getForward();
        forward.setDisplayName("§eNext page");
        ProfileSkull back = ControlSkulls.getBack();
        back.setDisplayName("§ePrevious page");
        ProfileSkull status;
        if (whitelistConfig.isWhitelistEnabled()) {
            status = ControlSkulls.getEnabled();
            status.setDisplayName("§aEnabled");
        } else {
            status = ControlSkulls.getDisabled();
            status.setDisplayName("§cDisabled");
        }
        status.onClick(whitelistGui::onStatusClick);
        back.onClick(whitelistGui::onPreviousPageClick);
        forward.onClick(whitelistGui::onNextPageClick);
        controlPane.addItem(forward.toGuiItem());
        controlPane.addItem(back.toGuiItem());
        controlPane.addItem(status.toGuiItem());
        controlPane.flipVertically(true);
        return controlPane;
    }
}
