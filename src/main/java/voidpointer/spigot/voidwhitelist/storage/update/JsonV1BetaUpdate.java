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
package voidpointer.spigot.voidwhitelist.storage.update;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistablePojo;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

final class JsonV1BetaUpdate implements JsonUpdate {
    @AutowiredLocale private static LocaleLog log;

    @Override public Collection<Whitelistable> performUpdate(final JsonElement root) {
        try {
            Collection<Whitelistable> updated = parseJson(root);
            root.getAsJsonObject().addProperty("version", StorageVersion.CURRENT.toString());
            return updated;
        } catch (final NullPointerException nullPointerException) {
            log.warn("Invalid JSON data for the specified version", nullPointerException);
            return null;
        }
    }

    private Collection<Whitelistable> parseJson(final JsonElement root) throws NullPointerException {
        LinkedList<Whitelistable> parsed = new LinkedList<>();
        root.getAsJsonObject().getAsJsonArray("whitelist").forEach(whitelistableElement -> {
            JsonObject whitelistableObject = whitelistableElement.getAsJsonObject();
            UUID uniqueId = UUID.fromString(whitelistableObject.get("uniqueId").getAsString());
            JsonElement expiresAtElement = whitelistableObject.get("expiresAt");
            parsed.add(JsonWhitelistablePojo.builder()
                    .uniqueId(uniqueId)
                    .expiresAt(expiresAtElement.isJsonNull() ? null : new Date(expiresAtElement.getAsLong()))
                    .createdAt(new Date())
                    .build());
        });
        return parsed;
    }
}
