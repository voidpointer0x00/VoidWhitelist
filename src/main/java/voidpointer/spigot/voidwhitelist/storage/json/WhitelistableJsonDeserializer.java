package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

final class WhitelistableJsonDeserializer implements JsonDeserializer<Whitelistable> {
    @Override public Whitelistable deserialize(final JsonElement json, final Type typeOfT,
                                                   final JsonDeserializationContext context) throws JsonParseException {
        final JsonElement uniqueIdField = json.getAsJsonObject().get(WhitelistableJsonSerializer.UNIQUE_ID_FIELD);
        if (!uniqueIdField.isJsonPrimitive())
            throw new JsonParseException("Unsupported JSON type for NAME_FIELD: " + uniqueIdField);

        final JsonElement expiresAtField = json.getAsJsonObject().get(WhitelistableJsonSerializer.EXPIRES_AT_FIELD);
        if (!(expiresAtField.isJsonNull() || expiresAtField.isJsonPrimitive()))
            throw new JsonParseException("Unsupported JSON type for EXPIRES_AT_FIELD: " + expiresAtField);

        JsonWhitelistableNamePojo whitelistablePojo = new JsonWhitelistableNamePojo();
        whitelistablePojo.setUniqueId(UUID.fromString(uniqueIdField.getAsString()));
        whitelistablePojo.setExpiresAt(!expiresAtField.isJsonNull() ? new Date(expiresAtField.getAsLong()) : null);
        return whitelistablePojo;
    }
}
