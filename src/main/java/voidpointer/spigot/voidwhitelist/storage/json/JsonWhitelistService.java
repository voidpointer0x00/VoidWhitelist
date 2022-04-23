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
package voidpointer.spigot.voidwhitelist.storage.json;

import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.MemoryWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

import java.io.File;
import java.util.Collection;

import static voidpointer.spigot.voidwhitelist.storage.StorageMethod.JSON;

public final class JsonWhitelistService extends MemoryWhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.json";

    private final File whitelistFile;

    public JsonWhitelistService(final File dataFolder) {
        whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        load();
    }

    @Override public StorageMethod getStorageMethod() {
        return JSON;
    }

    private void load() {
        if (!whitelistFile.exists()) {
            saveWhitelist();
            return;
        }

        Collection<Whitelistable> whitelistedPlayers = JsonWhitelist.parseJsonFile(whitelistFile);
        for (Whitelistable whitelistable : whitelistedPlayers)
            this.getWhitelist().add(whitelistable);
    }

    @Override protected void saveWhitelist() {
        JsonWhitelist.of(getWhitelist()).save(whitelistFile);
    }
}
