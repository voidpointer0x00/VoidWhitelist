/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.reflect.Type;

public final class WhitelistableJsonSerializer implements JsonSerializer<Whitelistable> {
    public static final String UNIQUE_ID_FIELD = "uniqueId";
    public static final String EXPIRES_AT_FIELD = "expiresAt";
    public static final String NAME_FIELD = "name";

    public static JsonElement serialize(final Whitelistable src) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(UNIQUE_ID_FIELD, new JsonPrimitive(src.getUniqueId().toString()));
        jsonObject.add(NAME_FIELD, new JsonPrimitive(src.getName()));
        if (src.isExpirable())
            jsonObject.add(EXPIRES_AT_FIELD, new JsonPrimitive(src.getExpiresAt().getTime()));
        else
            jsonObject.add(EXPIRES_AT_FIELD, JsonNull.INSTANCE);
        return jsonObject;
    }

    @Override public JsonElement serialize(final Whitelistable src, final Type typeOfSrc, final JsonSerializationContext context) {
        return serialize(src);
    }
}
