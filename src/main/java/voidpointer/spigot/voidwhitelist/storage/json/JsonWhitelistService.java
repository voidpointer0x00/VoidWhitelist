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

import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.MemoryWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static voidpointer.spigot.voidwhitelist.storage.StorageMethod.JSON;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ReconnectResult.FAIL;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ReconnectResult.SUCCESS;

public final class JsonWhitelistService extends MemoryWhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.json";
    public static final String AUTO_WHITELIST_FILE_NAME = "auto-whitelist.json";

    private final File whitelistFile;
    private final File autoWhitelistFile;

    public JsonWhitelistService(final File dataFolder) {
        whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        autoWhitelistFile = new File(dataFolder, AUTO_WHITELIST_FILE_NAME);
        load();
    }

    @Override public StorageMethod getStorageMethod() {
        return JSON;
    }

    @Override public ReconnectResult reconnect() {
        return load() ? SUCCESS : FAIL;
    }

    private boolean load() {
        return loadWhitelist() && loadAutoWhitelist();
    }

    private boolean loadAutoWhitelist() {
        if (!autoWhitelistFile.exists()) {
            saveAutoWhitelist();
            return true;
        }
        Optional<Map<UUID, AutoWhitelistNumber>> autoWhitelist = JsonAutoWhitelist.parseJsonFile(autoWhitelistFile);
        if (!autoWhitelist.isPresent())
            return false;
        getAutoWhitelist().putAll(autoWhitelist.get());
        return true;
    }

    private boolean loadWhitelist() {
        if (!whitelistFile.exists()) {
            saveWhitelist();
            return true;
        }

        Optional<Collection<Whitelistable>> whitelistedPlayers = JsonWhitelist.parseJsonFile(whitelistFile);
        for (final Whitelistable whitelistable : whitelistedPlayers.orElseGet(Collections::emptySet))
            this.getWhitelist().add(whitelistable);
        return whitelistedPlayers.isPresent();
    }

    @Override protected void saveWhitelist() {
        JsonWhitelist.of(getWhitelist()).save(whitelistFile);
    }

    @Override protected void saveAutoWhitelist() {
        JsonAutoWhitelist.save(getAutoWhitelist(), autoWhitelistFile);
    }
}
