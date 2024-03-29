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
import com.github.stefvanschie.inventoryframework.pane.Pane;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.task.AddProfileSkullTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;
import static voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher.fetchProfiles;

@Getter
public final class WhitelistGui extends AbstractGui {
    public static int MAJOR_VERSION_REQUIRED = 14;

    private final PaginatedPane whitelistPane;
    private final ConcurrentHashMap<Profile, ProfileScreen> profileScreens;
    private final OutlinePane controlPane;
    private WeakReference<AddProfileSkullTask> loadingTaskRef;
    @Setter(PROTECTED)
    private GuiItem enabledButton;
    @Setter(PROTECTED)
    private GuiItem disabledButton;

    public WhitelistGui() {
        super(new ChestGui(6, "§6VoidWhitelist"));
        profileScreens = new ConcurrentHashMap<>();
        whitelistPane = GuiPanes.createWhitelistPagesPane();
        addPane(whitelistPane);
        addPane(GuiPanes.getDelimiter());
        controlPane = GuiPanes.createControlPane(this);
        addPane(controlPane);
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
        profileSkull.getGuiItem().setAction(event -> {
            if (!isLoading())
                profileScreen.show(event.getWhoClicked());
        });
    }

    public void removeProfile(final ProfileSkull profileSkull) {
        profileScreens.remove(profileSkull.getProfile());
        getCurrentPage().removeItem(profileSkull.getGuiItem());
    }

    public void onRefresh(final InventoryClickEvent event) {
        final int availableSlots = availableProfileSlots();
        if (availableSlots == 0)
            return;
        getWhitelistService().findAll(getCurrentOffset(), availableSlots).thenAcceptAsync(whitelistableSet -> {
            if (whitelistableSet.isEmpty())
                return;
            fillCurrentPage(whitelistableSet);
        });
    }

    public void onStatusClick(final InventoryClickEvent event) {
        assert (enabledButton != null) && (disabledButton != null) : "Enable/disable buttons must be set";
        if (getWhitelistConfig().isWhitelistEnabled()) {
            getWhitelistConfig().disableWhitelist();
            getEventManager().callAsyncEvent(new WhitelistDisabledEvent());
            controlPane.removeItem(enabledButton);
            controlPane.insertItem(disabledButton, GuiPanes.STATUS_INDEX);
        } else {
            getWhitelistConfig().enableWhitelist();
            getEventManager().callAsyncEvent(new WhitelistEnabledEvent());
            controlPane.removeItem(disabledButton);
            controlPane.insertItem(enabledButton, GuiPanes.STATUS_INDEX);
        }
        update();
    }

    public void onNextPageClick(final InventoryClickEvent event) {
        if (isLoading())
            return;
        Optional<OutlinePane> nextPage = setToNextPageAndGet();
        if (nextPage.isEmpty())
            return;
        int capacity = availableProfileSlots();
        int offset = getCurrentOffset();
        getWhitelistService().findAll(offset, capacity).thenAcceptAsync(this::fillCurrentPage);
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
        clearNextPages();
        ConcurrentLinkedQueue<Profile> profiles = fetchProfiles(new ArrayList<>(whitelistable));
        AddProfileSkullTask loadingTask = new AddProfileSkullTask(this, profiles, whitelistable.size());
        loadingTask.runTaskTimerAsynchronously(getPlugin(), 0, 1L);
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
            if (nextPage.getItems().isEmpty())
                return Optional.of(getCurrentPage());
        }
        whitelistPane.setPage(whitelistPane.getPage() + 1);
        return Optional.of(nextPage);
    }

    private void clearNextPages() {
        for (int page = whitelistPane.getPages(); --page > whitelistPane.getPage();)
            whitelistPane.deletePage(page);
    }

    private int getCurrentOffset() {
        Pane currentPage = getCurrentPage();
        int capacity = availableProfileSlots();
        int offset = whitelistPane.getPage() * currentPage.getHeight() * currentPage.getLength();
        offset += (currentPage.getHeight() * currentPage.getLength() - capacity);
        return offset;
    }

    private boolean isLoading() {
        return (loadingTaskRef != null) && !loadingTaskRef.refersTo(null) && requireNonNull(loadingTaskRef.get()).isLoading();
    }

    private boolean isAtLastPage() {
        return whitelistPane.getPage() == (whitelistPane.getPages() - 1);
    }

    private @NonNull OutlinePane getCurrentPage() {
        return (OutlinePane) whitelistPane.getPanes(whitelistPane.getPage()).iterator().next();
    }
}
