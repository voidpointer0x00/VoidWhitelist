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

import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import lombok.NoArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access=PRIVATE)
public final class InputGui {

    public static void ask(final String title,
                           final HumanEntity human,
                           final Consumer<AnvilGui> setupAnvil,
                           final Predicate<AnvilGui> predicate,
                           final BiConsumer<String, HumanEntity> callback) {
        final AnvilGui gui = new AnvilGui(title);
        setupAnvil.accept(gui);
        gui.setOnGlobalClick(InputGui::cancelClickIfNotPlayerInventory);
        gui.setOnTopClick(event -> {
            boolean predicateResult = (predicate == null) || predicate.test(gui);
            if (predicateResult && (event.getSlot() == 2))
                callback.accept(gui.getRenameText(), human);
        });
        gui.show(human);
    }

    private static void cancelClickIfNotPlayerInventory(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER)
            event.setCancelled(true);
    }
}
