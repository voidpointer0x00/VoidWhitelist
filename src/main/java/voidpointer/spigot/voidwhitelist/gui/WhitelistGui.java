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
import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.task.LoadingTask;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

@Getter
public final class WhitelistGui {
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @AutowiredLocale private static LocaleLog locale;
    private final ChestGui gui;
    private final OutlinePane whitelistPane;
    private WeakReference<HumanEntity> viewer;
    private LoadingTask loadingTask;
    @Setter(AccessLevel.PRIVATE)
    private boolean isUpdating = false;

    public WhitelistGui() {
        gui = new ChestGui(6, "§6VoidWhitelist");
        gui.setOnGlobalClick(this::cancelClickIfNotPlayerInventory);
        gui.setOnClose(this::clearViewer);
        whitelistPane = new OutlinePane(7, 4);
        gui.addPane(whitelistPane);
    }

    public void stopLoading() {
        if (loadingTask != null) {
            loadingTask.cancel();
            gui.setTitle("§6VoidWhitelist");
            update();
        }
    }

    public void startLoading(final CountDownLatch countDownLatch, final int countDownStart) {
        loadingTask = new LoadingTask(this, countDownLatch, countDownStart);
        loadingTask.runTaskTimer(plugin, 0, 3);
    }

    public void addProfile(final Profile profile) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if ((skullMeta != null) && profile.getTexturesBase64().isPresent())
            setProfile(skullMeta, profile.toGameProfile());
        skullMeta.setDisplayName("§e" + profile.getName());
        head.setItemMeta(skullMeta);
        whitelistPane.addItem(new GuiItem(head));
        // TODO: actions on click
    }

    private void setProfile(final SkullMeta meta, final GameProfile toGameProfile) {
        try {
            setProfile0(meta, toGameProfile);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException exception) {
            locale.warn("Unable to set profile for skull", exception);
        }
    }

    private void setProfile0(final SkullMeta skullMeta, GameProfile profile)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        mtd.setAccessible(true);
        mtd.invoke(skullMeta, profile);
    }

    public void show(final HumanEntity humanEntity) {
        if ((viewer != null) && (viewer.get() != null))
            return;
        gui.show(humanEntity);
        viewer = new WeakReference<>(humanEntity);
    }

    public void update() {
        // we need to ensure that it's executing synchronously
        if (!isUpdating) {
            setUpdating(true);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                gui.update();
                setUpdating(false);
            });
        }
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
