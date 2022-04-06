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
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
final class ProfileScreen {
    private final WhitelistGui parent;
    private final ProfileSkull profileSkull;
    private final ChestGui screen;
    @Setter private GuiItem removeButton;

    ProfileScreen(final WhitelistGui parent, final ProfileSkull profileSkull) {
        this.parent = parent;
        this.profileSkull = profileSkull;
        screen = new ChestGui(4, "§6" + (profileSkull.getProfile().getName() != null
                ? profileSkull.getProfile().getName()
                : profileSkull.getProfile().getUuid().toString()));
        screen.setOnGlobalClick(parent::cancelClickIfNotPlayerInventory);
        screen.addPane(GuiPanes.createProfilePane(this));
    }

    public void show(final HumanEntity humanEntity) {
        screen.show(humanEntity);
    }

    public void back(final InventoryClickEvent event) {
        parent.show(event.getWhoClicked());
    }

    public void onRemoveButtonClick(final InventoryClickEvent event) {

    }
}
