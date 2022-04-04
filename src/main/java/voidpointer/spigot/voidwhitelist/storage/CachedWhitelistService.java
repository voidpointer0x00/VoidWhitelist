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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
 *  {@link #saveWhitelist()} methods. It will be invoked after any modification
 *  <em>({@link #add(UUID)}, {@link #remove(Whitelistable)} etc.)</em>to the
 *  cached whitelist.
 */
public abstract class CachedWhitelistService implements WhitelistService {
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Set<Whitelistable> cachedWhitelist = ConcurrentHashMap.newKeySet();

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final int limit) {
        if (cachedWhitelist.isEmpty())
            return completedFuture(Collections.emptySet());
        return supplyAsync(() -> {
            Whitelistable first = cachedWhitelist.iterator().next();
            Set<Whitelistable> subset = findAll0(first, limit);
            subset.add(first);
            return Collections.unmodifiableSet(subset);
        });
    }

    @Override public CompletableFuture<Set<Whitelistable>> findAll(final Whitelistable offset, final int limit) {
        if (cachedWhitelist.isEmpty())
            return completedFuture(Collections.emptySet());
        return supplyAsync(() -> Collections.unmodifiableSet(findAll0(offset, limit)));
    }

    private Set<Whitelistable> findAll0(final Whitelistable offset, final int limit) {
        Set<Whitelistable> subset = new HashSet<>();
        Iterator<Whitelistable> iterator = cachedWhitelist.iterator();
        while (iterator.hasNext() && !iterator.next().equals(offset))
            ;
        for (int index = 0; (index < limit) && iterator.hasNext(); index++, subset.add(iterator.next()))
            ;
        return subset;
    }

    @Override public CompletableFuture<Optional<Whitelistable>> findFirst() {
        if (cachedWhitelist.isEmpty())
            return completedFuture(Optional.empty());
        return completedFuture(Optional.of(cachedWhitelist.iterator().next()));
    }

    @Override public CompletableFuture<Optional<Whitelistable>> findLast() {
        if (cachedWhitelist.isEmpty())
            return completedFuture(Optional.empty());
        return completedFuture(Optional.of(cachedWhitelist.iterator().next()));
    }

    @Override public CompletableFuture<Integer> size() {
        return completedFuture(cachedWhitelist.size());
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final UUID uuid) {
        return supplyAsync(() -> {
            // Could've used Map for fast search operations, but who tf cares
            for (Whitelistable whitelistable : cachedWhitelist) {
                if (whitelistable.getUniqueId().equals(uuid))
                    return Optional.of(whitelistable);
            }
            return Optional.empty();
        });
    }

    @Override public CompletableFuture<Whitelistable> add(final UUID uuid, final Date expiresAt) {
        return supplyAsync(() -> {
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
        return supplyAsync(() -> {
            cachedWhitelist.remove(whitelistable);
            saveWhitelist();
            return true;
        });
    }

    protected abstract void saveWhitelist();
}
