package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.*;
import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.lang.reflect.Type;

final class VwPlayerJsonSerializer implements JsonSerializer<VwPlayer> {
    static final String NAME_FIELD = "name";
    static final String EXPIRES_AT_FIELD = "expiresAt";

    @Override public JsonElement serialize(final VwPlayer src, final Type typeOfSrc, final JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(NAME_FIELD, new JsonPrimitive(src.getName()));
        jsonObject.add(EXPIRES_AT_FIELD, new JsonPrimitive(src.getExpiresAt().getTime()));
        return jsonObject;
    }
}
