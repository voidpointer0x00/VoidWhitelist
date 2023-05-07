package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;
import voidpointer.spigot.voidwhitelist.storage.update.JsonUpdate;
import voidpointer.spigot.voidwhitelist.storage.update.JsonUpdateFactory;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptySet;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static voidpointer.spigot.voidwhitelist.storage.StorageVersion.CURRENT;
import static voidpointer.spigot.voidwhitelist.storage.StorageVersion.UNDEFINED;
import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.serialize;

public final class JsonWhitelist extends JsonStorage {
    private static final String VERSION_PROP = "version";
    private static final String WHITELIST_PROP = "whitelist";

    private final JsonObject whitelistAndMeta;
    private final JsonArray whitelist;

    public JsonWhitelist() {
        whitelistAndMeta = new JsonObject();
        whitelistAndMeta.add(VERSION_PROP, new JsonPrimitive(CURRENT.toString()));
        whitelist = new JsonArray();
        whitelistAndMeta.add(WHITELIST_PROP, whitelist);
    }

    public static JsonWhitelist of(final Collection<Whitelistable> allWhitelistable) {
        JsonWhitelist jsonWhitelist = new JsonWhitelist();
        jsonWhitelist.whitelist.addAll((JsonArray) gson.toJsonTree(allWhitelistable));
        return jsonWhitelist;
    }

    /**
     * <p>Reads all {@link Whitelistable} entries from a given JSON file.</p>
     *
     * <p>The file <b>must</b> have the following structure:</p>
     *
     * <pre>
     * {
     *     // see StorageVersion constants
     *     "version": "V2_BETA",
     *     "whitelist": [
     *          // Whitelistable fields for the given version
     *          {
     *              "uniqueId": "c55a15b5-896f-4c09-9c07-75ad36572aad",
     *              "name": "_voidpointer",
     *              "expiresAt": 1653578855958
     *          },
     *          ...
     *     ]
     * }
     * </pre>
     *
     * @return an optional collection full of {@link Whitelistable} entities from the given
     *      file or {@link Optional#empty()} if parsing failed due to any exception.
     */
    public static Optional<Collection<Whitelistable>> parseJsonFile(final @NonNull File jsonFile) {
        final String jsonContents = fileToString(jsonFile);
        if (jsonContents == null)
            return Optional.empty();
        try {
            JsonElement root = gson.fromJson(jsonContents, JsonElement.class);
            StorageVersion version = parseVersion(root);
            if (version != CURRENT)
                return Optional.ofNullable(performUpdate(version, root, jsonFile));

            Type whitelistType = new TypeToken<List<Whitelistable>>() {}.getType();
            Collection<Whitelistable> parsed = gson.fromJson(root.getAsJsonObject().get(WHITELIST_PROP), whitelistType);
            return Optional.of(parsed == null ? emptySet() : parsed);
        } catch (final JsonSyntaxException | NullPointerException jsonSyntaxException) {
            log.warn("Invalid JSON syntax in " + jsonFile.getName(), jsonSyntaxException);
            return Optional.empty();
        }
    }

    public void add(final @NonNull Whitelistable whitelistable) {
        whitelist.add(serialize(whitelistable));
    }

    /**
     * <p>Saves the whitelist into a specified file using UTF-8 encoding.</p>
     *
     * <p>Automatically logs any exception information in process.</p>
     *
     * @return {@code true} if saved successfully, {@code false} if any
     *      exception occurred in the process.
     */
    public boolean save(final @NonNull File destination) {
        return save(whitelistAndMeta, destination);
    }

    private static StorageVersion parseVersion(final JsonElement root) {
        String versionStr = root.getAsJsonObject().get(VERSION_PROP).getAsString();
        for (final StorageVersion storageVersion : StorageVersion.values())
            if (storageVersion.toString().equals(versionStr))
                return storageVersion;
        return UNDEFINED;
    }

    private static Collection<Whitelistable> performUpdate(
            final StorageVersion version,
            final JsonElement root,
            final File jsonFile
    ) {
        log.info("Performing a storage update from {0} to {1}, it might take a while.", version, CURRENT);
        Optional<JsonUpdate> update = JsonUpdateFactory.from(version);
        if (update.isEmpty()) {
            backupUpdateFailure(jsonFile, root);
            return null;
        }
        final long start = currentTimeMillis();
        Collection<Whitelistable> updated = update.get().performUpdate(root);
        if (null == updated) {
            backupUpdateFailure(jsonFile, root);
            return null;
        }
        of(updated).save(jsonFile);
        log.info("Successfully updated JSON storage from {0} to {1} for {2} sec.", version, CURRENT,
                MILLISECONDS.toSeconds(currentTimeMillis() - start));
        return updated;
    }

    private static void backupUpdateFailure(final File jsonFile, final JsonElement root) {
        if (save(root, new File(jsonFile.getParentFile(), jsonFile.getName() + ".bak"))) {
            log.info("Created backup .bak file for {0} due to update failure", jsonFile.getName());
        } else {
            log.severe("Unable to create backup file for {0}, data loss to be expected", jsonFile.getName());
            log.info("JSON to backup: {0}", root.toString());
        }
    }
}
