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
package voidpointer.spigot.voidwhitelist.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@ToString(onlyExplicitlyIncluded=true)
public final class Profile {
    @AutowiredLocale private static LocaleLog log;
    @Autowired(mapId="plugin")
    private static Plugin plugin;

    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID uuid;
    @Nullable
    @ToString.Include
    private String name;
    private Optional<String> texturesBase64 = Optional.empty();

    public GameProfile toGameProfile() {
        GameProfile gameProfile = new GameProfile(uuid, name);
        texturesBase64.ifPresent(s -> gameProfile.getProperties().put("textures", new Property("textures", s)));
        return gameProfile;
    }

    void fromJson(final JsonElement json) {
        name = getNameFromJson(json).orElse(null);
        texturesBase64 = getEncodedTexturesFromJson(json);
    }

    private Optional<String> getNameFromJson(final JsonElement json) {
        try {
            return Optional.of(json.getAsJsonObject().get("name").getAsString());
        } catch (final IllegalStateException notAJsonObject) {
            return Optional.empty();
        } catch (final NullPointerException invalidUuid) {
            log.warn("NPE: {0}", json.getAsJsonObject().get("errorMessage"));
            return Optional.empty();
        }
    }

    private Optional<String> getEncodedTexturesFromJson(final JsonElement json) throws RuntimeException {
        try {
            JsonArray properties = json.getAsJsonObject().get("properties").getAsJsonArray();
            Optional<JsonObject> texturesProperty = StreamSupport.stream(properties.spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .filter(property -> property.get("name").getAsString().equals("textures"))
                    .findFirst();
            return texturesProperty.map(jsonObject -> jsonObject.get("value").getAsString());
        } catch (IllegalStateException | NullPointerException ignoreInvalidJson) {
            return Optional.empty();
        }
    }
}
