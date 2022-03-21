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
import java.util.logging.Logger;

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
        JsonObject whitelistObject = new JsonObject();
        whitelistObject.add(VERSION_PROPERTY, new JsonPrimitive(StorageVersion.CURRENT.toString()));
        whitelistObject.add(WHITELIST_PROPERTY, gson.toJsonTree(getCachedWhitelist()));
        try {
            Files.write(gson.toJson(whitelistObject), whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.severe("An exception occurred while saving the whitelist: " + ioException.getMessage());
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
            log.severe("Invalid json syntax in whitelist file: " + jsonSyntaxException.getMessage());
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
            log.severe("An exception occurred while reading whitelist file contents: " + ioException.getMessage());
            return null;
        }
    }
}
