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
import com.google.gson.JsonParser;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

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

    public static ConcurrentLinkedQueue<Profile> fetchProfiles(final Iterable<UUID> uuids) {
        ConcurrentLinkedQueue<Profile> profiles = new ConcurrentLinkedQueue<>();
        for (UUID uuid : uuids) {
            fetchProfile(uuid).whenComplete((profile, thrown) -> {
                if (thrown == null) {
                    profiles.add(profile);
                } else {
                    profiles.add(new Profile(uuid));
                    log.warn("Couldn't complete profile "+uuid+" fetching", thrown);
                }
            });
        }
        return profiles;
    }

    public static CompletableFuture<Profile> fetchProfile(final UUID uuid) {
        if (profilesCache.asMap().containsKey(uuid))
            return completedFuture(profilesCache.getIfPresent(uuid));
        return supplyAsync(() -> {
            Profile profile = requestApi(uuid);
            if (profile.getTexturesBase64().isPresent())
                profilesCache.put(uuid, profile);
            return profile;
        });
    }

    private static Profile requestApi(final UUID uuid) {
        try {
            return requestApi0(uuid);
        } catch (IOException ioException) {
            log.warn("API call failed", ioException);
            return new Profile(uuid);
        }
    }

    private static Profile requestApi0(final UUID uuid) throws IOException {
        Profile profile = new Profile(uuid);
        profile.fromJson(JsonParser.parseReader(newConnectionReader(uuid)));
        return profile;
    }

    private static InputStreamReader newConnectionReader(final UUID uuid) throws IOException {
        return new InputStreamReader(new URL(API_URL + uuid.toString()).openStream());
    }
}
