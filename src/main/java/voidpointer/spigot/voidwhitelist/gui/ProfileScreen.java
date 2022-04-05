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
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

final class ProfileScreen {
    private final Gui parent;
    private final ChestGui screen;

    ProfileScreen(final WhitelistGui parent, final ProfileSkull profileSkull) {
        this.parent = parent.getGui();
        screen = new ChestGui(3, "§6" + (profileSkull.getProfile().getName() != null
                ? profileSkull.getProfile().getName()
                : profileSkull.getProfile().getUuid().toString()));
        screen.setOnGlobalClick(parent::cancelClickIfNotPlayerInventory);
        StaticPane mainPane = new StaticPane(9, 3);
        screen.addPane(mainPane);
        mainPane.fillWith(GuiPanes.getBackgroundItem());
        GuiItem profileSkullItem = profileSkull.getGuiItem().copy();
        profileSkullItem.setAction(event -> {});
        mainPane.addItem(profileSkullItem, 4, 0);
        ProfileSkull back = ControlSkulls.getBack();
        back.setDisplayName("§eBack to whitelist");
        back.getGuiItem().setAction(this::back);
        mainPane.addItem(back.getGuiItem(), 8, 2);
    }

    private void back(final InventoryClickEvent event) {
        parent.show(event.getWhoClicked());
    }

    public void show(final HumanEntity humanEntity) {
        screen.show(humanEntity);
    }
}
