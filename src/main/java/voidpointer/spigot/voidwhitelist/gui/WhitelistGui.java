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
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.lang.ref.WeakReference;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Phaser;

@Getter
public final class WhitelistGui {
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @AutowiredLocale private static LocaleLog locale;
    private final ChestGui gui;
    private final PaginatedPane whitelistPane;
    private WeakReference<HumanEntity> viewer;
    @Setter(AccessLevel.PRIVATE)
    private Phaser updatingStatus = new Phaser();

    public WhitelistGui() {
        gui = new ChestGui(6, "§6VoidWhitelist");
        gui.setOnGlobalClick(this::cancelClickIfNotPlayerInventory);
        gui.setOnClose(this::clearViewer);
        whitelistPane = GuiPanes.createWhitelistPane();
        gui.addPane(whitelistPane);
        gui.addPane(GuiPanes.getDelimiter());
        gui.addPane(GuiPanes.createControlPane(this));
    }

    public int availableProfileSlots() {
        OutlinePane currentPage = getCurrentPage();
        return currentPage.getHeight() * currentPage.getLength() - currentPage.getItems().size();
    }

    public void addProfile(final Profile profile) throws ConcurrentModificationException {
        ProfileSkull profileSkull = ProfileSkull.of(profile).setupDisplayInfo();
        getCurrentPage().addItem(profileSkull.toGuiItem());
        // TODO: actions on click
    }

    private OutlinePane getCurrentPage() {
        return (OutlinePane) whitelistPane.getPanes(whitelistPane.getPage()).iterator().next();
    }

    public void show(final HumanEntity humanEntity) {
        if ((viewer != null) && (viewer.get() != null))
            return;
        gui.show(humanEntity);
        viewer = new WeakReference<>(humanEntity);
    }

    public void update() {
        if (updatingStatus.getRegisteredParties() > 0)
            updatingStatus.arriveAndAwaitAdvance();
        updatingStatus.register();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            gui.update();
            updatingStatus.arriveAndDeregister();
        });
    }

    public void onStatusClick(final InventoryClickEvent event) {

    }

    public void onNextPageClick(final InventoryClickEvent event) {

    }

    public void onPreviousPageClick(final InventoryClickEvent event) {

    }

    private void cancelClickIfNotPlayerInventory(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER)
            event.setCancelled(true);
    }

    private void clearViewer(final InventoryCloseEvent inventoryCloseEvent) {
        viewer = null;
    }
}
