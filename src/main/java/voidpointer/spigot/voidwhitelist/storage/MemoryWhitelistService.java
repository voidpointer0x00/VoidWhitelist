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
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * An abstract and thread-safe implementation for all {@link WhitelistService} operations.
 *
 * This implementation stores all {@link Whitelistable} entries in memory
 *  using {@link java.util.Set} and does not implement saving the whitelist.
 * 
 * In order to save the cached whitelist entries you need to implement
 *  {@link #saveWhitelist()} methods. It will be invoked upon any modification
 *  ({@link #add(UUID, String, Date)}, {@link #remove(Whitelistable)} to the cached whitelist.
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public abstract class MemoryWhitelistService implements AutoWhitelistService {
    @Getter private Set<Whitelistable> whitelist = ConcurrentHashMap.newKeySet();
    /* possibly we could join the two structures, but I prefer keeping things simple KISS */
    private Map<UUID, AutoWhitelistNumber> autoWhitelist = new ConcurrentHashMap<>();

    @Override public CompletableFuture<Optional<AutoWhitelistNumber>> getAutoWhitelistNumberOf(final UUID uniqueId) {
        return supplyAsync(() -> Optional.ofNullable(autoWhitelist.get(uniqueId)));
    }

    @Override public void shutdown() {
        saveWhitelist();
        whitelist.clear();
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int limit) {
        if (whitelist.isEmpty())
            return completedFuture(Collections.emptySet());
        return supplyAsync(() -> {
            Set<Whitelistable> subset = findAll0(0, limit);
            return Collections.unmodifiableSet(subset);
        });
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int offset, final int limit) {
        if (whitelist.isEmpty())
            return completedFuture(Collections.emptySet());
        return supplyAsync(() -> Collections.unmodifiableSet(findAll0(offset, limit)));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private Set<Whitelistable> findAll0(final int offset, final int limit) {
        Set<Whitelistable> subset = new HashSet<>();
        Iterator<Whitelistable> iterator = whitelist.iterator();
        for (int index = 0; (index < offset) && iterator.hasNext(); index++, iterator.next())
            ;
        for (int index = 0; (index < limit) && iterator.hasNext(); index++, subset.add(iterator.next()))
            ;
        return subset;
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        return supplyAsync(() -> {
            // Could've used Map for fast search operations, but who tf cares
            for (Whitelistable whitelistable : whitelist) {
                if (whitelistable.getUniqueId().equals(uuid))
                    return Optional.of(whitelistable);
            }
            return Optional.empty();
        });
    }

    @Override public CompletableFuture<Optional<Whitelistable>> add(
            final UUID uuid, final String name, final Date expiresAt) {
        return add(uuid, name, expiresAt, AutoWhitelistNumber.ZERO.get());
    }

    @Override public CompletableFuture<Optional<Whitelistable>> add(
            final UUID uuid, final String name, final Date expiresAt, final int timesAutoWhitelisted) {
        return supplyAsync(() -> {
            final Whitelistable whitelistable = new SimpleWhitelistable(uuid, name, expiresAt);
            if (!whitelist.add(whitelistable)) {
                whitelist.remove(whitelistable);
                whitelist.add(whitelistable);
            }
            saveWhitelist();
            autoWhitelist.put(uuid, AutoWhitelistNumber.of(timesAutoWhitelisted));
            saveAutoWhitelist();
            return Optional.of(whitelistable);
        });
    }

    @Override public CompletableFuture<Optional<Whitelistable>> update(final @NonNull Whitelistable whitelistable) {
        return supplyAsync(() -> {
            whitelist.remove(whitelistable);
            whitelist.add(whitelistable);
            return Optional.of(whitelistable);
        });
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        return supplyAsync(() -> {
            whitelist.remove(whitelistable);
            saveWhitelist();
            return true;
        });
    }

    protected abstract void saveWhitelist();

    protected abstract void saveAutoWhitelist();
}
