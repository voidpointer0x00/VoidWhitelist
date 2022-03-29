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
import voidpointer.spigot.voidwhitelist.storage.CachedWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;
import voidpointer.spigot.voidwhitelist.storage.UnknownVersionException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

public final class JsonWhitelistService extends CachedWhitelistService {
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
    private final File whitelistFile;

    public JsonWhitelistService(final File dataFolder) {
        whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        load();
    }

    private void load() {
        if (!whitelistFile.exists()) {
            saveWhitelist();
            return;
        }

        List<Whitelistable> whitelistedPlayers = readAndParseWhitelistFileContents(whitelistFile);
        if (null != whitelistedPlayers)
            for (Whitelistable whitelistable : whitelistedPlayers)
                this.getCachedWhitelist().add(whitelistable);
    }

    @Override protected void saveWhitelist() {
        JsonObject whitelistObject = new JsonObject();
        whitelistObject.add(VERSION_PROPERTY, new JsonPrimitive(StorageVersion.CURRENT.toString()));
        whitelistObject.add(WHITELIST_PROPERTY, gson.toJsonTree(getCachedWhitelist()));
        try {
            Files.write(gson.toJson(whitelistObject), whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.severe("An exception occurred while saving the whitelist", ioException);
        }
    }

    private List<Whitelistable> readAndParseWhitelistFileContents(final File whitelistFile) {
        String whitelistFileContents = readWhitelistFileContents(whitelistFile);
        if (null == whitelistFileContents)
            return null;

        try {
            JsonElement root = parser.parse(whitelistFileContents);
            StorageVersion version = parseVersion(root);
            if (version != StorageVersion.CURRENT) {
                // TODO: implement version updates
                throw new RuntimeException("Different storage versions are not supported.");
            }
            Type whitelistType = new TypeToken<List<Whitelistable>>() {}.getType();
            return gson.fromJson(root.getAsJsonObject().get(WHITELIST_PROPERTY), whitelistType);
        } catch (final JsonSyntaxException jsonSyntaxException) {
            log.severe("Invalid json syntax in whitelist file", jsonSyntaxException);
            return null;
        } catch (UnknownVersionException unknownVersionException) {
            log.severe("Unknown storage version");
            return null;
        }
    }

    private StorageVersion parseVersion(final JsonElement root) throws UnknownVersionException {
        String versionStr = root.getAsJsonObject().get(VERSION_PROPERTY).getAsString();
        for (StorageVersion storageVersion : StorageVersion.values())
            if (storageVersion.toString().equals(versionStr))
                return storageVersion;
        throw new UnknownVersionException();
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
