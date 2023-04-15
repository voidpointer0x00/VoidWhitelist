package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelistedNumber;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

final class JsonAutoWhitelist extends JsonStorage {
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
    public static Optional<Map<UUID, TimesAutoWhitelistedNumber>> parseJsonFile(final File jsonFile) {
        final String jsonContents = fileToString(jsonFile);
        if (jsonContents == null)
            return Optional.empty();
        try {
            JsonObject dataAndMeta = gson.fromJson(jsonContents, JsonObject.class);
            JsonArray autoWhitelist = dataAndMeta.getAsJsonArray("autoWhitelist");
            if (autoWhitelist == null)
                return Optional.empty();
            Map<UUID, TimesAutoWhitelistedNumber> parsed = new HashMap<>();
            autoWhitelist.forEach(jsonElement -> parsed.put(
                    UUID.fromString(jsonElement.getAsJsonObject().get("uniqueId").getAsString()),
                    TimesAutoWhitelistedNumber.of(jsonElement.getAsJsonObject().get("timesAutoWhitelisted").getAsInt())
            ));
            return Optional.of(parsed);
        } catch (final JsonSyntaxException | NullPointerException | IllegalArgumentException exception) {
            log.warn("Invalid JSON syntax in " + jsonFile.getName(), exception);
            return Optional.empty();
        }
    }

    public static void save(final Map<UUID, TimesAutoWhitelistedNumber> autoWhitelist, final File destination) {
        final JsonArray jsonAutoWhitelist = new JsonArray();
        autoWhitelist.forEach((uniqueId, timesAutoWhitelisted) -> {
            JsonObject jsonAutoWhitelistEntry = new JsonObject();
            jsonAutoWhitelistEntry.addProperty("uniqueId", uniqueId.toString());
            jsonAutoWhitelistEntry.addProperty("timesAutoWhitelisted", timesAutoWhitelisted.get());
            jsonAutoWhitelist.add(jsonAutoWhitelistEntry);
        });
        final JsonObject dataAndMeta = new JsonObject();
        dataAndMeta.add("autoWhitelist", jsonAutoWhitelist);
        save(dataAndMeta, destination);
    }
}
