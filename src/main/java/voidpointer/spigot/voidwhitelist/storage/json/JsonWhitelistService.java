package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import voidpointer.spigot.voidwhitelist.storage.NotWhitelistedException;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class JsonWhitelistService implements WhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateJsonSerializer())
            .registerTypeAdapter(Date.class, new DateJsonDeserializer())
            .serializeNulls()
            .create();

    private final File whitelistFile;
    private Map<String, Date> whitelist;

    public JsonWhitelistService(final File dataFolder) {
        whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        load();
    }

    @Override public boolean addToWhitelist(final String nickname) {
        whitelist.put(nickname, null);
        save();
        return true;
    }

    @Override public boolean addToWhitelist(final String nickname, final Date expiresAt) {
        whitelist.put(nickname, expiresAt);
        save();
        return true;
    }

    @Override public boolean isWhitelisted(final String nickname) {
        return whitelist.containsKey(nickname);
    }

    @Override public Date getExpiresAt(final String nickname) throws NotWhitelistedException {
        Date expiresAt = whitelist.get(nickname);
        if (null == expiresAt) {
            throw new NotWhitelistedException("Trying to getExpiresAt() of a not whitelisted player \"" + nickname + "\"");
        }
        return expiresAt;
    }

    @Override public List<String> getWhitelistedNicknames() {
        Date currentDate = new Date();
        return whitelist.entrySet().stream()
                .filter(entry -> (NEVER_EXPIRES == entry.getValue()) || entry.getValue().after(currentDate))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    @Override public boolean removeFromWhitelist(final String nickname) {
        whitelist.remove(nickname);
        save();
        return true;
    }

    private void load() {
        if (!whitelistFile.exists()) {
            whitelist = new TreeMap<>();
            save();
            return;
        }
        Type whitelistType = new TypeToken<Map<String, Date>>() {}.getType();
        String whitelistFileContents;
        try {
            whitelistFileContents = Files.toString(whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            System.err.println("An exception occurred while reading whitelist file contents: " + ioException.getMessage());
            whitelist = new TreeMap<>();
            return;
        }
        try {
            whitelist = gson.fromJson(whitelistFileContents, whitelistType);
        } catch (final JsonSyntaxException jsonSyntaxException) {
            System.err.println("Invalid json syntax in whitelist file: " + jsonSyntaxException.getMessage());
            whitelist = new TreeMap<>();
            return;
        }
    }

    private void save() {
        String whitelistInJson = gson.toJson(whitelist);
        try {
            Files.write(whitelistInJson, whitelistFile, Charset.defaultCharset());
        } catch (IOException ioException) {
            System.err.println("An exception occurred while trying to save the whitelist: " + ioException.getMessage());
        }
    }
}
