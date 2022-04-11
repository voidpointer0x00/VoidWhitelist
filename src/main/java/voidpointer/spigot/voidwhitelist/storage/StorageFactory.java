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
package voidpointer.spigot.voidwhitelist.storage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.storage.db.HibernateWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;

@RequiredArgsConstructor
public final class StorageFactory {
    @NonNull private final Plugin plugin;

    public WhitelistService loadStorage(final WhitelistConfig whitelistConfig) {
        switch (whitelistConfig.getStorageMethod()) {
            case JSON:
                return new JsonWhitelistService(plugin.getDataFolder());
            case SERIAL:
                return new SerialWhitelistService(plugin.getDataFolder());
            case DATABASE:
                return new HibernateWhitelistService(plugin);
            default:
                throw new UnsupportedOperationException("Unknown storage method: "
                                                        + whitelistConfig.getStorageMethod());
        }
    }
}
