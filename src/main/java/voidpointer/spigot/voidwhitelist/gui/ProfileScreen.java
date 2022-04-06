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

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.entity.HumanEntity;

final class ProfileScreen {
    private final ChestGui screen;

    ProfileScreen(final WhitelistGui parent, final ProfileSkull profileSkull) {
        screen = new ChestGui(3, "§6" + (profileSkull.getProfile().getName() != null
                ? profileSkull.getProfile().getName()
                : profileSkull.getProfile().getUuid().toString()));
        screen.setOnGlobalClick(parent::cancelClickIfNotPlayerInventory);
        screen.addPane(GuiPanes.createProfilePane(parent, profileSkull));
    }

    public void show(final HumanEntity humanEntity) {
        screen.show(humanEntity);
    }
}
