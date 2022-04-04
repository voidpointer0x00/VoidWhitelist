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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

final class WhitelistableJsonDeserializer implements JsonDeserializer<Whitelistable> {
    @AutowiredLocale private static LocaleLog log;

    @Override public Whitelistable deserialize(final JsonElement json, final Type typeOfT,
                                                   final JsonDeserializationContext context) throws JsonParseException {
        final JsonElement uniqueIdField = json.getAsJsonObject().get(WhitelistableJsonSerializer.UNIQUE_ID_FIELD);
        if (!uniqueIdField.isJsonPrimitive())
            throw new JsonParseException("Unsupported JSON type for NAME_FIELD: " + uniqueIdField);

        final JsonElement nameField = json.getAsJsonObject().get(WhitelistableJsonSerializer.NAME_FIELD);
        if (!(nameField.isJsonNull() || nameField.isJsonPrimitive()))
            throw new JsonParseException("Unsupported JSON type for NAME_FIELD: " + nameField);
        if (nameField.isJsonNull())
            log.warn("Encountered \"null\" name while parsing whitelistable, please, update it manually!");

        final JsonElement expiresAtField = json.getAsJsonObject().get(WhitelistableJsonSerializer.EXPIRES_AT_FIELD);
        if (!(expiresAtField.isJsonNull() || expiresAtField.isJsonPrimitive()))
            throw new JsonParseException("Unsupported JSON type for EXPIRES_AT_FIELD: " + expiresAtField);

        final JsonElement createdAtField = json.getAsJsonObject().get(WhitelistableJsonSerializer.CREATED_AT_FIELD);
        if (!createdAtField.isJsonPrimitive())
            throw new JsonParseException("Unsupported JSON type for CREATED_AT_FIELD: " + createdAtField);

        JsonWhitelistablePojo whitelistablePojo = new JsonWhitelistablePojo();
        whitelistablePojo.setUniqueId(UUID.fromString(uniqueIdField.getAsString()));
        whitelistablePojo.setName(nameField.isJsonNull() ? null : nameField.getAsString());
        whitelistablePojo.setExpiresAt(!expiresAtField.isJsonNull() ? new Date(expiresAtField.getAsLong()) : null);
        try {
            whitelistablePojo.setCreatedAt(new Date(createdAtField.getAsLong()));
        } catch (NumberFormatException numberFormatException) {
            throw new JsonParseException("Invalid createdAt Date format", numberFormatException);
        }
        return whitelistablePojo;
    }
}
