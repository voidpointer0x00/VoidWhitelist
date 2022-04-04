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
import com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.function.Consumer;

public final class ProfileSkull {
    @AutowiredLocale private static LocaleLog locale;

    private final Profile profile;
    private final ItemStack skull;
    private final GuiItem guiSkull;

    private ProfileSkull(final Profile profile) {
        this.profile = profile;
        skull = new ItemStack(Material.PLAYER_HEAD);
        guiSkull = new GuiItem(skull);
        applyTexturesIfPossible();
    }

    public static ProfileSkull of(final Profile profile) {
        return new ProfileSkull(profile);
    }

    public ProfileSkull setupDisplayInfo() {
        assert skull.getItemMeta() instanceof SkullMeta;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName("§e" + profile.getName());
        meta.setLore(Collections.singletonList("§eUUID: §6" + profile.getUuid()));
        skull.setItemMeta(meta);
        return this;
    }

    public void setDisplayName(final String displayName) {
        assert skull.getItemMeta() instanceof SkullMeta;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(displayName);
        skull.setItemMeta(meta);
    }

    public void onClick(Consumer<InventoryClickEvent> consumer) {
        guiSkull.setAction(consumer);
    }

    public GuiItem toGuiItem() {
        return guiSkull;
    }

    private void applyTexturesIfPossible() {
        try {
            if (profile.getTexturesBase64().isPresent())
                applyTextures0();
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException exception) {
            locale.warn("Unable to set profile for skull", exception);
        }
    }

    private void applyTextures0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assert skull.getItemMeta() instanceof SkullMeta;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        Method setProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
        setProfileMethod.setAccessible(true);
        setProfileMethod.invoke(skullMeta, profile.toGameProfile());
        skull.setItemMeta(skullMeta);
    }
}
