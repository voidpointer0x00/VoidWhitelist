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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Phaser;

@Getter
final class ProfileScreen {
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @Autowired private static WhitelistService whitelistService;
    @AutowiredLocale private static LocaleLog log;
    private final Phaser updatingStatus = new Phaser();

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
        final Profile profile = profileSkull.getProfile();
        whitelistService.find(profile.getUuid())
                .exceptionally(this::onFindException)
                .thenAcceptAsync(whitelistableOptional -> {
                    if (!whitelistableOptional.isPresent()) {
                        onNotFound();
                        return;
                    }
                    whitelistService.remove(whitelistableOptional.get())
                            .thenAccept(isRemoved -> {
                                if (isRemoved)
                                    onRemoved();
                                else
                                    onNotRemoved();
                            })
                            .exceptionally(this::onRemoveException);
                });
    }

    private void onRemoved() {
        parent.removeProfile(profileSkull.getGuiItem());
        plugin.getServer().getScheduler().runTask(plugin, () -> parent.show(screen.getViewers().get(0)));
        // TODO: add WhitelistGui::refresh() method - public version of fillCurrentPage
    }

    private void onNotRemoved() {
        ItemMeta removeButtonMeta = removeButton.getItem().getItemMeta();
        if (removeButtonMeta == null) {
            removeButtonMeta.setDisplayName("§cRemove operation failed");
            removeButtonMeta.setLore(Arrays.asList("§6Check console log info, if you have access"));
        }
        removeButton.getItem().setItemMeta(removeButtonMeta);
        update();
    }

    private void onNotFound() {
        ItemMeta removeButtonMeta = removeButton.getItem().getItemMeta();
        if (removeButtonMeta != null)
            removeButtonMeta.setDisplayName("§cPlayer not found");
        removeButton.getItem().setItemMeta(removeButtonMeta);
        update();
    }

    private Optional<Whitelistable> onFindException(final Throwable throwable) {
        onNotRemoved();
        return Optional.empty();
    }

    private Void onRemoveException(final Throwable throwable) {
        log.warn("Couldn't remove "+profileSkull.getProfile()+" from the whitelist", throwable);
        onNotRemoved();
        return null;
    }

    public void update() {
        // TODO: extract to an abstract VoidGui (?) class
        if (updatingStatus.getRegisteredParties() > 0)
            updatingStatus.arriveAndAwaitAdvance();
        updatingStatus.register();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            screen.update();
            updatingStatus.arriveAndDeregister();
        });
    }
}
