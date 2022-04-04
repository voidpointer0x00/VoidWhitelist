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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.net.Profile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

public final class ProfileSkull {
    @AutowiredLocale private static LocaleLog locale;

    private final Profile profile;
    private final ItemStack skull;

    private ProfileSkull(final Profile profile) {
        this.profile = profile;
        skull = new ItemStack(Material.PLAYER_HEAD);
        applyTexturesIfPossible();
        setupName();
        setupLore();
    }

    private void setupName() {
        assert skull.getItemMeta() instanceof SkullMeta;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName("§e" + profile.getName());
        skull.setItemMeta(meta);
    }

    private void setupLore() {
        assert skull.getItemMeta() instanceof SkullMeta;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setLore(Collections.singletonList("§eUUID: §6" + profile.getUuid()));
        skull.setItemMeta(meta);
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

    public static ProfileSkull of(final Profile profile) {
        return new ProfileSkull(profile);
    }

    public GuiItem toGuiItem() {
        return new GuiItem(skull);
    }
}
