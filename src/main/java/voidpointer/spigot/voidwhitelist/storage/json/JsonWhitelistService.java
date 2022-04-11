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

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.MemoryWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;
import voidpointer.spigot.voidwhitelist.storage.update.JsonUpdate;
import voidpointer.spigot.voidwhitelist.storage.update.JsonUpdateFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static voidpointer.spigot.voidwhitelist.storage.StorageVersion.CURRENT;

public final class JsonWhitelistService extends MemoryWhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.json";
    private static final String VERSION_PROPERTY = "version";
    private static final String WHITELIST_PROPERTY = "whitelist";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateJsonSerializer())
            .registerTypeAdapter(Date.class, new DateJsonDeserializer())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableInstanceCreator())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableJsonSerializer())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableJsonDeserializer())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    private static final JsonParser parser = new JsonParser();

    @AutowiredLocale private static LocaleLog log;
    private final JsonUpdateFactory updateFactory = new JsonUpdateFactory();
    private final File whitelistFile;
    private boolean wasUpdated = false;

    public JsonWhitelistService(final File dataFolder) {
        whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        load();
    }

    private void load() {
        if (!whitelistFile.exists()) {
            saveWhitelist();
            return;
        }

        Collection<Whitelistable> whitelistedPlayers = readAndParseWhitelistFileContents(whitelistFile);
        for (Whitelistable whitelistable : whitelistedPlayers)
            this.getCachedWhitelist().add(whitelistable);
        if (wasUpdated) {
            saveWhitelist();
            wasUpdated = false;
        }
    }

    @Override protected void saveWhitelist() {
        JsonObject whitelistObject = new JsonObject();
        whitelistObject.add(VERSION_PROPERTY, new JsonPrimitive(CURRENT.toString()));
        whitelistObject.add(WHITELIST_PROPERTY, gson.toJsonTree(getCachedWhitelist()));
        try {
            Files.write(gson.toJson(whitelistObject), whitelistFile, Charset.defaultCharset());
        } catch (final IOException ioException) {
            log.severe("An exception occurred while saving the whitelist", ioException);
        }
    }

    private Collection<Whitelistable> readAndParseWhitelistFileContents(final File whitelistFile) {
        String whitelistFileContents = readWhitelistFileContents(whitelistFile);
        if (null == whitelistFileContents)
            return Collections.emptyList();

        try {
            JsonElement root = parser.parse(whitelistFileContents);
            StorageVersion version = parseVersion(root);
            if (version != CURRENT)
                return performUpdate(version, root);

            Type whitelistType = new TypeToken<List<Whitelistable>>() {}.getType();
            return gson.fromJson(root.getAsJsonObject().get(WHITELIST_PROPERTY), whitelistType);
        } catch (final JsonSyntaxException jsonSyntaxException) {
            log.severe("Invalid json syntax in whitelist file", jsonSyntaxException);
            backup();
            return Collections.emptyList();
        }
    }

    private Collection<Whitelistable> performUpdate(final StorageVersion version, final JsonElement root) {
        log.info("Performing a storage update from {0} to {1}, it might take a while.", version, CURRENT);
        Optional<JsonUpdate> update = updateFactory.from(version);
        if (!update.isPresent()) {
            backup();
            return Collections.emptyList();
        }
        final long start = System.currentTimeMillis();
        Collection<Whitelistable> updated = update.get().performUpdate(root);
        if (updated == null) {
            backup();
            return Collections.emptyList();
        }
        final long end = System.currentTimeMillis();
        wasUpdated = true;
        log.info("Successfully updated JSON storage from {0} to {1} for {2} sec.", version, CURRENT,
                MILLISECONDS.toSeconds(end - start));
        return updated;
    }

    private void backup() {
        try {
            Files.copy(whitelistFile, new File(whitelistFile.getAbsolutePath() + ".bak"));
            log.info("Created a backup {0}.bak file", whitelistFile.getName());
        } catch (final IOException ioException) {
            log.warn("Unable to create a backup .bak file for " + whitelistFile.getName(), ioException);
        }
    }

    private StorageVersion parseVersion(final JsonElement root) {
        String versionStr = root.getAsJsonObject().get(VERSION_PROPERTY).getAsString();
        for (StorageVersion storageVersion : StorageVersion.values())
            if (storageVersion.toString().equals(versionStr))
                return storageVersion;
        return StorageVersion.UNDEFINED;
    }

    private String readWhitelistFileContents(final File whitelistFile) {
        try {
            return Files.toString(whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.severe("An exception occurred while reading whitelist file contents ", ioException);
            return null;
        }
    }
}
