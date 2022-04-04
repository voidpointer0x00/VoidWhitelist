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
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.lang.ref.WeakReference;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Phaser;

@Getter
public final class WhitelistGui {
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @Autowired private static EventManager eventManager;
    @Autowired private static WhitelistConfig whitelistConfig;
    @AutowiredLocale private static LocaleLog locale;
    private final ChestGui gui;
    private final PaginatedPane whitelistPane;
    private final OutlinePane controlPane;
    private WeakReference<HumanEntity> viewer;
    @Setter(AccessLevel.PRIVATE)
    private Phaser updatingStatus = new Phaser();
    @Setter(AccessLevel.PROTECTED)
    private GuiItem enabledButton;
    @Setter(AccessLevel.PROTECTED)
    private GuiItem disabledButton;

    public WhitelistGui() {
        gui = new ChestGui(6, "§6VoidWhitelist");
        gui.setOnGlobalClick(this::cancelClickIfNotPlayerInventory);
        gui.setOnClose(this::clearViewer);
        whitelistPane = GuiPanes.createWhitelistPane();
        gui.addPane(whitelistPane);
        gui.addPane(GuiPanes.getDelimiter());
        controlPane = GuiPanes.createControlPane(this);
        gui.addPane(controlPane);
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
        assert (enabledButton != null) && (disabledButton != null) : "Enable/disable buttons must be set";
        if (whitelistConfig.isWhitelistEnabled()) {
            whitelistConfig.disableWhitelist();
            eventManager.callAsyncEvent(new WhitelistDisabledEvent());
            controlPane.removeItem(enabledButton);
            controlPane.addItem(disabledButton);
        } else {
            whitelistConfig.enableWhitelist();
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());
            controlPane.removeItem(disabledButton);
            controlPane.addItem(enabledButton);
        }
        update();
    }

    public void onNextPageClick(final InventoryClickEvent event) {
        // TODO: #fillCurrentPage() that will fill current page with profiles
        //  to avoid repeating the code with add command.
        // TODO: check whether current page is empty and fill only if it's not
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
