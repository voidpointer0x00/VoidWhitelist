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
package voidpointer.spigot.voidwhitelist.uuid;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

final class OnlineUUIDFetcher {
    private static final String UUID_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final Gson gson = new GsonBuilder().create();
    @AutowiredLocale private static LocaleLog log;
    private static final Cache<String, UUID> onlineUuidCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6L, TimeUnit.HOURS)
            .build();

    public static CompletableFuture<Optional<UUID>> getUUID(final String name) {
        UUID uniqueId = onlineUuidCache.getIfPresent(name);
        if (uniqueId != null)
            return CompletableFuture.completedFuture(Optional.of(uniqueId));

        return CompletableFuture.supplyAsync(() -> {
            UUID uniqueIdResponse = callApi(name);
            if (uniqueIdResponse == null)
                return Optional.empty();

            Optional<UUID> optionalUUID = Optional.of(uniqueIdResponse);
            onlineUuidCache.put(name, optionalUUID.get());
            return optionalUUID;
        });
    }

    private static UUID callApi(final String name) {
        try {
            String id = requestApiUrl(name);
            String uuid = idToUuid(id);
            return UUID.fromString(uuid);
        } catch (final FileNotFoundException userNotFound) {
            log.warn("Requested unknown players UUID: {0}", userNotFound.getMessage());
            return null;
        } catch (final IOException ioException) {
            log.warn("Unable to request Mojang API", ioException);
            return null;
        } catch (final IllegalArgumentException illegalArgumentException) {
            log.warn("Invalid UUID format", illegalArgumentException);
            return null;
        } catch (final NullPointerException nullPointerException) {
            /* 204 No Content API response, meaning no associated player profile found */
            return null;
        }
    }

    static String idToUuid(final String id) {
        StringBuilder uuidBuilder = new StringBuilder(id.length() + 4);
        for (int index = 0; index < id.length(); index++) {
            uuidBuilder.append(id.charAt(index));
            if ((index == 7) || (index == 11) || (index == 15) || (index == 19))
                uuidBuilder.append('-');
        }
        return uuidBuilder.toString();
    }

    private static String requestApiUrl(final String name) throws IOException, NullPointerException {
        return gson.fromJson(newApiRequestConnection(name), MojangUUIDResponse.class).id;
    }

    private static InputStreamReader newApiRequestConnection(final String name) throws IOException {
        return new InputStreamReader(new URL(UUID_API_URL + name).openStream());
    }
}
