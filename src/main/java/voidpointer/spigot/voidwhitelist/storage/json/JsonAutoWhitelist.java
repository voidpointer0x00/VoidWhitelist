package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelisted;
import voidpointer.spigot.voidwhitelist.storage.db.TimesAutoWhitelistedModel;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class JsonAutoWhitelist extends JsonStorage {
    /**
     * <code>
     *     {
     *         "autoWhitelist": [
     *              {
     *                  "uniqueId": "c55a15b5-896f-4c09-9c07-75ad36572aad",
     *                  "timesAutoWhitelisted": 1
     *              }, ...
     *         ]
     *     }
     * </code>
     */
    static Optional<Set<TimesAutoWhitelisted>> parseJsonFile(final File jsonFile) {
        final String jsonContents = fileToString(jsonFile);
        if (jsonContents == null)
            return Optional.empty();
        try {
            JsonObject dataAndMeta = gson.fromJson(jsonContents, JsonObject.class);
            JsonArray autoWhitelist = dataAndMeta.getAsJsonArray("autoWhitelist");
            if (autoWhitelist == null)
                return Optional.empty();
            final Set<TimesAutoWhitelisted> parsed = new HashSet<>();
            autoWhitelist.forEach(jsonElement -> parsed.add(
                    TimesAutoWhitelisted.of(UUID.fromString(jsonElement.getAsJsonObject().get("uniqueId").getAsString()),
                            jsonElement.getAsJsonObject().get("timesAutoWhitelisted").getAsInt())
            ));
            return Optional.of(parsed);
        } catch (final JsonSyntaxException | NullPointerException | IllegalArgumentException exception) {
            log.warn("Invalid JSON syntax in " + jsonFile.getName(), exception);
            return Optional.empty();
        }
    }

    static void save(final Map<UUID, TimesAutoWhitelisted> autoWhitelist, final File destination) {
        final JsonAutoWhitelist jsonAutoWhitelist = new JsonAutoWhitelist();
        autoWhitelist.forEach(jsonAutoWhitelist::add);
        jsonAutoWhitelist.save(destination);
    }

    private final JsonArray jsonAutoWhitelist = new JsonArray();

    public void add(final TimesAutoWhitelistedModel timesAutoWhitelistedNumber) {
        add(timesAutoWhitelistedNumber.getUniqueId(), timesAutoWhitelistedNumber);
    }

    private void add(final UUID uniqueId, final TimesAutoWhitelisted timesAutoWhitelisted) {
        JsonObject jsonAutoWhitelistEntry = new JsonObject();
        jsonAutoWhitelistEntry.addProperty("uniqueId", uniqueId.toString());
        jsonAutoWhitelistEntry.addProperty("timesAutoWhitelisted", timesAutoWhitelisted.get());
        jsonAutoWhitelist.add(jsonAutoWhitelistEntry);
    }

    public boolean save(final File destination) {
        final JsonObject wrapper = new JsonObject();
        wrapper.add("autoWhitelist", jsonAutoWhitelist);
        return save(wrapper, destination);
    }
}
