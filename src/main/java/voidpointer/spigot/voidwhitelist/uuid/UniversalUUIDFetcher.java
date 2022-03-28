package voidpointer.spigot.voidwhitelist.uuid;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class UniversalUUIDFetcher implements UUIDFetcher {
    private static final String UUID_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final Gson gson = new GsonBuilder().create();
    @AutowiredLocale private static LocaleLog log;
    private final Cache<String, UUID> onlineUuidCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6L, TimeUnit.HOURS)
            .build();
    private final Cache<String, UUID> offlineUuidCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6, TimeUnit.HOURS)
            .build();

    private final Function<String, CompletableFuture<Optional<UUID>>> defaultMethod;

    public UniversalUUIDFetcher(final boolean isOnlineMode) {
        this.defaultMethod = isOnlineMode
                ? this::getOnlineUUID
                : (name) -> CompletableFuture.completedFuture(Optional.of(getOfflineUUID(name)));
    }

    @Override public CompletableFuture<Optional<UUID>> getUUID(final String name) {
        return defaultMethod.apply(name);
    }

    @Override public UUID getOfflineUUID(final String name) {
        UUID uniqueId = offlineUuidCache.getIfPresent(name);
        if (uniqueId != null)
            return uniqueId;
        uniqueId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        offlineUuidCache.put(name, uniqueId);
        return uniqueId;
    }

    @Override public CompletableFuture<Optional<UUID>> getOnlineUUID(final String name) {
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

    private UUID callApi(final String name) {
        try {
            String id = requestApiUrl(name);
            String uuid = idToUuid(id);
            return UUID.fromString(uuid);
        } catch (IOException ioException) {
            log.warn("Unable to request Mojang API", ioException);
            return null;
        } catch (IllegalArgumentException illegalArgumentException) {
            log.warn("Invalid UUID format", illegalArgumentException);
            return null;
        }
    }

    private String idToUuid(final String id) {
        StringBuilder uuidBuilder = new StringBuilder(id.length() + 4);
        for (int index = 0; index < id.length(); index++) {
            uuidBuilder.append(id.charAt(index));
            if ((index == 7) || (index == 11) || (index == 15) || (index == 19))
                uuidBuilder.append('-');
        }
        return uuidBuilder.toString();
    }

    private String requestApiUrl(final String name) throws IOException {
        return gson.fromJson(newApiRequestConnection(name), MojangUUIDResponse.class).id;
    }

    private InputStreamReader newApiRequestConnection(final String name) throws IOException {
        return new InputStreamReader(new URL(UUID_API_URL + name).openStream());
    }
}
