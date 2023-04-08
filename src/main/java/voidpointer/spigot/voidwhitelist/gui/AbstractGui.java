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
import com.github.stefvanschie.inventoryframework.pane.Pane;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Optional;
import java.util.concurrent.Phaser;

import static lombok.AccessLevel.PROTECTED;

@Setter(value=PROTECTED)
public abstract class AbstractGui {
    @Getter
    @AutowiredLocale
    private static LocaleLog localeLog;
    @Getter
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;
    @Getter
    @Autowired
    private static WhitelistConfig whitelistConfig;
    @Getter
    @Autowired
    private static EventManager eventManager;
    @Getter
    @Autowired(mapId="plugin")
    private static Plugin plugin;

    private final Phaser updatingStatus = new Phaser();

    @Getter private final ChestGui screen;

    protected AbstractGui(final ChestGui screen) {
        this.screen = screen;
        this.screen.setOnGlobalClick(this::cancelClickIfNotPlayerInventory);
    }

    public void update() {
        if (updatingStatus.getRegisteredParties() > 0)
            updatingStatus.arriveAndAwaitAdvance();
        updatingStatus.register();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            screen.update();
            updatingStatus.arriveAndDeregister();
        });
    }

    public Optional<HumanEntity> getViewer() {
        if (screen.getViewers().isEmpty())
            return Optional.empty();
        return Optional.of(screen.getViewers().get(0));
    }

    public void show(final HumanEntity humanEntity) {
        screen.show(humanEntity);
    }

    protected final void addPane(final Pane pane) {
        screen.addPane(pane);
    }

    protected void cancelClickIfNotPlayerInventory(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER)
            event.setCancelled(true);
    }
}
