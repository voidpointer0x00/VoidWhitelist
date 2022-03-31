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
package voidpointer.spigot.voidwhitelist.storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class CachedWhitelistService implements WhitelistService {
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Set<Whitelistable> cachedWhitelist = new HashSet<>();

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            // Could've used Map for fast search operations, but who tf cares
            for (Whitelistable whitelistable : cachedWhitelist) {
                if (whitelistable.getUniqueId().equals(uuid))
                    return Optional.of(whitelistable);
            }
            return Optional.empty();
        });
    }

    @Override public CompletableFuture<Whitelistable> add(final UUID uuid) {
        return add(uuid, Whitelistable.NEVER_EXPIRES);
    }

    @Override public CompletableFuture<Whitelistable> add(final UUID uuid, final Date expiresAt) {
        return CompletableFuture.supplyAsync(() -> {
            Whitelistable whitelistable = new SimpleWhitelistable(uuid, expiresAt);
            if (!cachedWhitelist.add(whitelistable)) {
                cachedWhitelist.remove(whitelistable);
                cachedWhitelist.add(whitelistable);
            }
            saveWhitelist();
            return whitelistable;
        });
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        return CompletableFuture.supplyAsync(() -> {
            cachedWhitelist.remove(whitelistable);
            saveWhitelist();
            return true;
        });
    }

    protected abstract void saveWhitelist();
}
