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
import lombok.AccessLevel;
import lombok.Getter;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

public abstract class AbstractJsonUpdate implements JsonUpdate {
    @AutowiredLocale
    @Getter(AccessLevel.PROTECTED)
    private static LocaleLog log;

    @Override public Collection<Whitelistable> performUpdate(final JsonElement root) {
        try {
            Collection<Whitelistable> updated = updateJson(root);
            root.getAsJsonObject().addProperty("version", StorageVersion.CURRENT.toString());
            return updated;
        } catch (final NullPointerException nullPointerException) {
            log.warn("Invalid JSON data for the specified version", nullPointerException);
            return null;
        }
    }

    private Collection<Whitelistable> updateJson(final JsonElement root) throws NullPointerException {
        return stream(root.getAsJsonObject().getAsJsonArray("whitelist").spliterator(), true)
                .map(this::update)
                .collect(Collectors.toList());
    }

    protected abstract Whitelistable update(final JsonElement jsonElement);
}
