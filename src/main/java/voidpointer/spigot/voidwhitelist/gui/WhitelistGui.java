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
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.task.AddProfileSkullTask;

import java.lang.ref.WeakReference;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Phaser;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;
import static voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher.fetchProfiles;

@Getter
public final class WhitelistGui {
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @Autowired private static EventManager eventManager;
    @Autowired private static WhitelistConfig whitelistConfig;
    @Autowired private static WhitelistService whitelistService;
    @AutowiredLocale private static LocaleLog locale;
    private final ChestGui gui;
    private final PaginatedPane whitelistPane;
    private final ConcurrentHashMap<Profile, ProfileScreen> profileScreens;
    private final OutlinePane controlPane;
    private final Phaser updatingStatus = new Phaser();
    private WeakReference<AddProfileSkullTask> loadingTaskRef;
    @Setter(PROTECTED)
    private GuiItem enabledButton;
    @Setter(PROTECTED)
    private GuiItem disabledButton;
    // TODO: add a refresh button

    public WhitelistGui() {
        profileScreens = new ConcurrentHashMap<>();
        gui = new ChestGui(6, "§6VoidWhitelist");
        gui.setOnGlobalClick(this::cancelClickIfNotPlayerInventory);
        whitelistPane = GuiPanes.createWhitelistPagesPane();
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
        getCurrentPage().addItem(profileSkull.getGuiItem());
        ProfileScreen profileScreen = new ProfileScreen(this, profileSkull);
        profileScreens.put(profile, profileScreen);
        profileSkull.getGuiItem().setAction(event -> profileScreen.show(event.getWhoClicked()));
    }

    public void removeProfile(final GuiItem profileItem) {
        getCurrentPage().removeItem(profileItem);
    }

    public void show(final HumanEntity humanEntity) {
        gui.show(humanEntity);
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
            controlPane.insertItem(disabledButton, GuiPanes.STATUS_INDEX);
        } else {
            whitelistConfig.enableWhitelist();
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());
            controlPane.removeItem(disabledButton);
            controlPane.insertItem(enabledButton, GuiPanes.STATUS_INDEX);
        }
        update();
    }

    public void onNextPageClick(final InventoryClickEvent event) {
        if (isLoading())
            return;
        Optional<OutlinePane> nextPage = setToNextPageAndGet();
        if (!nextPage.isPresent())
            return;
        int capacity = availableProfileSlots();
        int offset = whitelistPane.getPage() * nextPage.get().getHeight() * nextPage.get().getLength();
        offset += (nextPage.get().getHeight() * nextPage.get().getLength() - capacity);
        whitelistService.findAll(offset, capacity).thenAcceptAsync(this::fillCurrentPage);
    }

    public void onPreviousPageClick(final InventoryClickEvent event) {
        if (isLoading())
            return;
        if (whitelistPane.getPage() == 0)
            return;
        whitelistPane.setPage(whitelistPane.getPage() - 1);
        update();
    }

    public void fillCurrentPage(final Set<Whitelistable> whitelistable) {
        if (whitelistable.isEmpty()) {
            update();
            return;
        }
        ConcurrentLinkedQueue<Profile> profiles = fetchProfiles(whitelistable.stream()
                .map(Whitelistable::getUniqueId)
                .collect(Collectors.toList()));
        AddProfileSkullTask loadingTask = new AddProfileSkullTask(this, profiles, whitelistable.size());
        loadingTask.runTaskTimerAsynchronously(plugin, 0, 1L);
        loadingTaskRef = new WeakReference<>(loadingTask);
    }

    public Optional<OutlinePane> setToNextPageAndGet() {
        if (availableProfileSlots() != 0)
            return Optional.of(getCurrentPage());
        OutlinePane nextPage;
        if (isAtLastPage()) {
            if (getCurrentPage().getItems().isEmpty())
                return Optional.empty(); // can't create a new page while the current one is empty
            nextPage = GuiPanes.createWhitelistPagePane();
            whitelistPane.addPane(whitelistPane.getPages(), nextPage);
        } else {
            nextPage = (OutlinePane) whitelistPane.getPanes(whitelistPane.getPage() + 1).iterator().next();
        }
        whitelistPane.setPage(whitelistPane.getPage() + 1);
        return Optional.of(nextPage);
    }

    void cancelClickIfNotPlayerInventory(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getType() != InventoryType.PLAYER)
            event.setCancelled(true);
    }

    private boolean isLoading() {
        // TODO: drop Java 8 support and use Java 16 API after release
        //  loadingTaskRef.refersTo(null) instead of (null != loadingTask)
        AddProfileSkullTask loadingTask = loadingTaskRef.get();
        return (null != loadingTask) && loadingTask.isLoading();
    }

    private boolean isAtLastPage() {
        return whitelistPane.getPage() == (whitelistPane.getPages() - 1);
    }

    private @NonNull OutlinePane getCurrentPage() {
        return (OutlinePane) whitelistPane.getPanes(whitelistPane.getPage()).iterator().next();
    }
}
