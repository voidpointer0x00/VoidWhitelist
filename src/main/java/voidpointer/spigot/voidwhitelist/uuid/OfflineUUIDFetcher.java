package voidpointer.spigot.voidwhitelist.uuid;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class OfflineUUIDFetcher implements UUIDFetcher {
    private final Cache<String, UUID> uniqueIdCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6, TimeUnit.HOURS)
            .build();

    @Override public UUID getUUID(final String name) {
        UUID uniqueId = uniqueIdCache.getIfPresent(name);
        if (uniqueId != null)
            return uniqueId;
        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        uniqueIdCache.put(name, uniqueId);
        return uniqueId;
    }
}
