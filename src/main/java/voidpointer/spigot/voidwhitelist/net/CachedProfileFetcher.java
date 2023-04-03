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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class CachedProfileFetcher {
    private static final String API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Cache<UUID, Profile> profilesCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();
    @AutowiredLocale private static LocaleLog log;
    private static JsonParser jsonParser;

    public static ConcurrentLinkedQueue<Profile> fetchProfiles(final Iterable<Whitelistable> whitelistables) {
        ConcurrentLinkedQueue<Profile> profiles = new ConcurrentLinkedQueue<>();
        for (Whitelistable whitelistable : whitelistables) {
            fetchProfile(whitelistable).whenComplete((profile, thrown) -> {
                if (thrown == null) {
                    profiles.add(profile);
                } else {
                    profiles.add(new Profile(whitelistable));
                    log.warn("Couldn't complete profile "+whitelistable.getUniqueId()+" fetching", thrown);
                }
            });
        }
        return profiles;
    }

    public static CompletableFuture<String> fetchName(final UUID uniqueId) {
        if (profilesCache.asMap().containsKey(uniqueId))
            return completedFuture(profilesCache.asMap().get(uniqueId).getName());
        return supplyAsync(() -> {
            try {
                //noinspection deprecation
                JsonElement jsonElement = getJsonParser().parse(newConnectionReader(uniqueId));
                if (!jsonElement.isJsonObject()) {
                    log.warn("Profile with name for UUID {0} not found.", uniqueId);
                    return null;
                }
                if (!jsonElement.getAsJsonObject().has("name")) {
                    log.warn("Invalid UUID ({0}): {1}", uniqueId,
                            jsonElement.getAsJsonObject().get("errorMessage").getAsString());
                    return null;
                }
                return jsonElement.getAsJsonObject().get("name").getAsString();
            } catch (final Exception exception) {
                log.warn("API name fetch failed", exception);
                return null;
            }
        });
    }

    public static CompletableFuture<Profile> fetchProfile(final Whitelistable whitelistable) {
        if (profilesCache.asMap().containsKey(whitelistable.getUniqueId()))
            return completedFuture(profilesCache.getIfPresent(whitelistable.getUniqueId()));
        return supplyAsync(() -> {
            Profile profile = requestApi(whitelistable);
            if (profile.getTexturesBase64().isPresent())
                profilesCache.put(whitelistable.getUniqueId(), profile);
            return profile;
        });
    }

    public static Profile removeCachedProfile(final UUID uuid) {
        return profilesCache.asMap().remove(uuid);
    }

    private static Profile requestApi(final Whitelistable whitelistable) {
        try {
            return requestApi0(whitelistable);
        } catch (final IOException ioException) {
            log.warn("API call failed", ioException);
            return new Profile(whitelistable);
        }
    }

    private static Profile requestApi0(final Whitelistable whitelistable) throws IOException {
        Profile profile = new Profile(whitelistable);
        //noinspection deprecation
        profile.fromJson(getJsonParser().parse(newConnectionReader(whitelistable.getUniqueId())));
        return profile;
    }

    private static InputStreamReader newConnectionReader(final UUID uuid) throws IOException {
        return new InputStreamReader(new URL(API_URL + uuid.toString()).openStream());
    }

    private static JsonParser getJsonParser() {
        if (jsonParser == null)
            //noinspection deprecation
            jsonParser = new JsonParser();
        return jsonParser;
    }
}
