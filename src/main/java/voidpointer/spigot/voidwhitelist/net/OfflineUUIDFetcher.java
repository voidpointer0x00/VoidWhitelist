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

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OfflineUUIDFetcher {
    private static final Cache<String, UUID> offlineUuidCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6, TimeUnit.HOURS)
            .build();

    public static CompletableFuture<Optional<UUID>> getUUID(final String name) {
        UUID uniqueId = offlineUuidCache.getIfPresent(name);
        if (uniqueId != null)
            return CompletableFuture.completedFuture(Optional.of(uniqueId));
        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        offlineUuidCache.put(name, uniqueId);
        return CompletableFuture.completedFuture(Optional.of(uniqueId));
    }
}
