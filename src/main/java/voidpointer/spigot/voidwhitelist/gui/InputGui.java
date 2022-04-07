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
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;
import static org.bukkit.Material.NAME_TAG;

@NoArgsConstructor(access=PRIVATE)
public final class InputGui {

    public static void ask(final String title,
                           final String hint,
                           final HumanEntity human,
                           final Predicate<AnvilGui> predicate,
                           final BiConsumer<String, HumanEntity> callback) {
        final AnvilGui gui = new AnvilGui(title);
        setHintItem(hint, gui);
        setResultingItem(gui);
        gui.setOnGlobalClick(InputGui::cancelClickIfNotPlayerInventory);
        gui.setOnTopClick(event -> {
            boolean predicateResult = (predicate == null) || predicate.test(gui);
            if (predicateResult && (event.getSlot() == 2))
                callback.accept(gui.getRenameText(), human);
        });
        gui.show(human);
    }

    private static void setHintItem(final String hint, final AnvilGui anvilGui) {
        ItemStack nameTag = new ItemStack(NAME_TAG);
        ItemMeta meta = nameTag.getItemMeta();
        assert meta != null : "ItemMeta for name tag item cannot be null";
        meta.setDisplayName(hint);
        nameTag.setItemMeta(meta);
        StaticPane hintPane = new StaticPane(1, 1);
        anvilGui.getFirstItemComponent().addPane(hintPane);
        hintPane.addItem(new GuiItem(nameTag), 0, 0);
    }

    private static void setResultingItem(final AnvilGui anvilGui) {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        assert meta != null : "ItemMeta for clock item cannot be null";
        meta.setDisplayName("§eEnter new date");
        StaticPane resultingPane = new StaticPane(1, 1);
        resultingPane.addItem(new GuiItem(clock), 0, 0);
        anvilGui.getResultComponent().addPane(resultingPane);
    }

    private static void cancelClickIfNotPlayerInventory(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER)
            event.setCancelled(true);
    }
}
