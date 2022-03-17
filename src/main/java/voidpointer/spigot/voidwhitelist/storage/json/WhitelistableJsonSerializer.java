package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.reflect.Type;

final class WhitelistableJsonSerializer implements JsonSerializer<Whitelistable> {
    static final String UNIQUE_ID_FIELD = "uniqueId";
    static final String EXPIRES_AT_FIELD = "expiresAt";

    @Override public JsonElement serialize(final Whitelistable src, final Type typeOfSrc, final JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(UNIQUE_ID_FIELD, new JsonPrimitive(src.getUniqueId().toString()));
        jsonObject.add(EXPIRES_AT_FIELD, new JsonPrimitive(src.getExpiresAt().getTime()));
        return jsonObject;
    }
}
