package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import voidpointer.spigot.voidwhitelist.WhitelistableName;

import java.lang.reflect.Type;
import java.util.Date;

final class VwPlayerJsonDeserializer implements JsonDeserializer<WhitelistableName> {
    @Override public WhitelistableName deserialize(final JsonElement json, final Type typeOfT,
                                                   final JsonDeserializationContext context) throws JsonParseException {
        final JsonElement nameField = json.getAsJsonObject().get(VwPlayerJsonSerializer.NAME_FIELD);
        if (!nameField.isJsonPrimitive())
            throw new JsonParseException("Unsupported JSON type for NAME_FIELD: " + nameField);

        final JsonElement expiresAtField = json.getAsJsonObject().get(VwPlayerJsonSerializer.EXPIRES_AT_FIELD);
        if (!(expiresAtField.isJsonNull() || expiresAtField.isJsonPrimitive()))
            throw new JsonParseException("Unsupported JSON type for EXPIRES_AT_FIELD: " + expiresAtField);

        JsonWhitelistableNamePojo vwPlayer = new JsonWhitelistableNamePojo();
        vwPlayer.setName(nameField.getAsString());
        vwPlayer.setExpiresAt(!expiresAtField.isJsonNull() ? new Date(expiresAtField.getAsLong()) : null);
        return vwPlayer;
    }
}
