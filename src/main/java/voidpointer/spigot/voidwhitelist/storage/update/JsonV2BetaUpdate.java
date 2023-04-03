package voidpointer.spigot.voidwhitelist.storage.update;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistablePojo;

import java.util.Date;
import java.util.UUID;

import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.EXPIRES_AT_FIELD;
import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.NAME_FIELD;
import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.UNIQUE_ID_FIELD;

public final class JsonV2BetaUpdate extends AbstractJsonUpdate {
    @Override protected Whitelistable update(final JsonElement whitelistableRoot) {
        final JsonObject whitelistableObject = whitelistableRoot.getAsJsonObject();
        final UUID uniqueId = UUID.fromString(whitelistableObject.get(UNIQUE_ID_FIELD).getAsString());
        final JsonElement expiresAtElement = whitelistableObject.get(EXPIRES_AT_FIELD);
        final String name = whitelistableObject.get(NAME_FIELD).getAsString();
        return JsonWhitelistablePojo.builder()
                .uniqueId(uniqueId)
                .name(name)
                .expiresAt(expiresAtElement.isJsonNull() ? null : new Date(expiresAtElement.getAsLong()))
                /*  This is the new field when updating from V2_BETA to V3
                 * Even though it is already set to 0 by default, I think
                 * it's better to keep the track of what's changed here. */
                .timesAutoWhitelisted(0)
                .build();
    }
}
