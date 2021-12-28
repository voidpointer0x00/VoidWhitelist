package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.WhitelistableName;
import voidpointer.spigot.voidwhitelist.storage.SimpleWhitelistableName;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class JsonWhitelistService implements WhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateJsonSerializer())
            .registerTypeAdapter(Date.class, new DateJsonDeserializer())
            .registerTypeAdapter(WhitelistableName.class, new VwPlayerInstanceCreator())
            .registerTypeAdapter(WhitelistableName.class, new VwPlayerJsonSerializer())
            .registerTypeAdapter(WhitelistableName.class, new VwPlayerJsonDeserializer())
            .serializeNulls()
            .create();

    private final Logger log;
    private final File whitelistFile;
    private Map<String, WhitelistableName> whitelist;

    public JsonWhitelistService(final Logger log, final File dataFolder) {
        this.log = log;
        whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        load();
    }

    @Override public CompletableFuture<WhitelistableName> findByName(final String name) {
        return CompletableFuture.supplyAsync(() -> whitelist.get(name));
    }

    @Override
    public CompletableFuture<WhitelistableName> addName(final String name) {
        return addName(name, Whitelistable.NEVER_EXPIRES);
    }

    @Override
    public CompletableFuture<WhitelistableName> addName(final String name, final Date expiresAt) {
        return CompletableFuture.supplyAsync(() -> {
            SimpleWhitelistableName vwPlayer = new SimpleWhitelistableName(name, expiresAt);
            whitelist.put(name, vwPlayer);
            save();
            return vwPlayer;
        });
    }

    @Override
    public CompletableFuture<List<String>> getAllWhitelistedNames() {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(whitelist.keySet()));
    }

    @Override
    public CompletableFuture<Boolean> removeFromWhitelist(final WhitelistableName whitelistableName) {
        return CompletableFuture.supplyAsync(() -> {
            whitelist.remove(whitelistableName.toString());
            save();
            return true;
        });
    }

    private void load() {
        whitelist = new TreeMap<>();
        if (!whitelistFile.exists()) {
            save();
            return;
        }

        List<WhitelistableName> whitelistedPlayers = readAndParseWhitelistFileContents(whitelistFile);
        if (null != whitelistedPlayers)
            for (WhitelistableName whitelistedPlayer : whitelistedPlayers)
                whitelist.put(whitelistedPlayer.toString(), whitelistedPlayer);
    }

    private void save() {
        String whitelistInJson = gson.toJson(whitelist.values());
        try {
            Files.write(whitelistInJson, whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.severe("An exception occurred while trying to save the whitelist: " + ioException.getMessage());
        }
    }

    private List<WhitelistableName> readAndParseWhitelistFileContents(final File whitelistFile) {
        String whitelistFileContents = readWhitelistFileContents(whitelistFile);
        if (null == whitelistFileContents)
            return null;

        try {
            Type whitelistType = new TypeToken<List<WhitelistableName>>() {}.getType();
            return gson.fromJson(whitelistFileContents, whitelistType);
        } catch (final JsonSyntaxException jsonSyntaxException) {
            log.severe("Invalid json syntax in whitelist file: " + jsonSyntaxException.getMessage());
            return null;
        }
    }

    private String readWhitelistFileContents(final File whitelistFile) {
        try {
            return Files.toString(whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.severe("An exception occurred while reading whitelist file contents: " + ioException.getMessage());
            return null;
        }
    }
}
