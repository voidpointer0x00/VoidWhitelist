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
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.net.CachedProfileFetcher;
import voidpointer.spigot.voidwhitelist.net.Profile;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistablePojo;

import java.util.Date;
import java.util.UUID;

import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.CREATED_AT_FIELD;
import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.EXPIRES_AT_FIELD;
import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.UNIQUE_ID_FIELD;

public class JsonV2BetaUpdate extends AbstractJsonUpdate {
    @Override protected Whitelistable update(final JsonElement jsonElement) {
        JsonObject whitelistableObject = jsonElement.getAsJsonObject();
        UUID uniqueId = UUID.fromString(whitelistableObject.get(UNIQUE_ID_FIELD).getAsString());
        JsonElement expiresAtElement = whitelistableObject.get(EXPIRES_AT_FIELD);
        Date createdAt = new Date(whitelistableObject.get(CREATED_AT_FIELD).getAsLong());
        Profile profile = CachedProfileFetcher.fetchProfile(uniqueId).join();
        if (profile.getName() == null)
            getLog().warn("The name for the {0} was not found, you will need to add it manually", uniqueId);
        return JsonWhitelistablePojo.builder()
                .uniqueId(uniqueId)
                .name(profile.getName())
                .expiresAt(expiresAtElement.isJsonNull() ? null : new Date(expiresAtElement.getAsLong()))
                .createdAt(createdAt)
                .build();
    }
}