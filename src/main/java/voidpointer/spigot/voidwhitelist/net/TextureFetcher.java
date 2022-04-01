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
import com.google.gson.Gson;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class TextureFetcher {
    private static final String API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final Gson gson = new Gson();
    private static final Cache<UUID, Optional<String>> textureCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();
    @AutowiredLocale private static LocaleLog log;

    public static CompletableFuture<Map<UUID, Optional<String>>> fetchTextures(
            final Collection<UUID> uuids,
            final CountDownLatch countDownLatch) {
        if (countDownLatch.getCount() != uuids.size())
            throw new IllegalArgumentException("The count of countDownLatch must match the size of uuids");
        return supplyAsync(() -> {
            Map<UUID, Optional<String>> textures = Collections.synchronizedMap(new HashMap<>());
            for (final UUID uuid : uuids) {
                fetchTexture(uuid).whenComplete((result, throwable) -> {
                    if (throwable == null)
                        textures.put(uuid, result);
                    else
                        log.warn("Couldn't finish texture fetching", throwable);
                    countDownLatch.countDown();
                });
            }
            await(countDownLatch);
            return textures;
        });
    }

    public static CompletableFuture<Optional<String>> fetchTexture(final UUID uuid) {
        if (textureCache.asMap().containsKey(uuid))
            return completedFuture(textureCache.getIfPresent(uuid));
        return supplyAsync(() -> {
            Optional<String> texture = callApi(uuid);
            if (texture.isPresent())
                textureCache.put(uuid, texture);
            return texture;
        });
    }

    private static Optional<String> callApi(final UUID uuid) {
        try {
            return Optional.ofNullable(callApi0(uuid));
        } catch (IOException ioException) {
            log.warn("API call failed", ioException);
            return Optional.empty();
        }
    }

    private static String callApi0(final UUID uuid) throws IOException {
        TextureResponse response = gson.fromJson(newConnectionReader(uuid), TextureResponse.class);
        if (response == null)
            return null;
        return response.getTextures();
    }

    private static InputStreamReader newConnectionReader(final UUID uuid) throws IOException {
        return new InputStreamReader(new URL(API_URL + uuid.toString()).openStream());
    }

    private static void await(final CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException interruptedException) {
            log.warn("Couldn't finish textures fetching", interruptedException);
        }
    }
}
