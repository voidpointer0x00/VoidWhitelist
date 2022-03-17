package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.CachedWhitelistService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public final class JsonWhitelistService extends CachedWhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateJsonSerializer())
            .registerTypeAdapter(Date.class, new DateJsonDeserializer())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableInstanceCreator())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableJsonSerializer())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableJsonDeserializer())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private final Logger log;
    private final File whitelistFile;

    public JsonWhitelistService(final Logger log, final File dataFolder) {
        this.log = log;
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
        String whitelistInJson = gson.toJson(this.getCachedWhitelist());
        try {
            Files.write(whitelistInJson, whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.severe("An exception occurred while trying to save the whitelist: " + ioException.getMessage());
        }
    }

    private List<Whitelistable> readAndParseWhitelistFileContents(final File whitelistFile) {
        String whitelistFileContents = readWhitelistFileContents(whitelistFile);
        if (null == whitelistFileContents)
            return null;

        try {
            Type whitelistType = new TypeToken<List<Whitelistable>>() {}.getType();
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
