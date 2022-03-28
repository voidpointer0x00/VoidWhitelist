package voidpointer.spigot.voidwhitelist.uuid;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OfflineUUIDFetcher implements UUIDFetcher {
    private final Cache<String, UUID> uniqueIdCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6, TimeUnit.HOURS)
            .build();

    @Override public CompletableFuture<Optional<UUID>> getUUID(final String name) {
        UUID uniqueId = uniqueIdCache.getIfPresent(name);
        if (uniqueId != null)
            return CompletableFuture.completedFuture(Optional.of(uniqueId));
        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        uniqueIdCache.put(name, uniqueId);
        return CompletableFuture.completedFuture(Optional.of(uniqueId));
    }
}
