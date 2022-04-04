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

import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {
    /**
     * Finds {@code limit} elements starting from the very first {@link Whitelistable}
     *      in the storage inclusively.
     *
     * @param limit maximum number of elements to search for.
     *
     * @return {@code limit} number of elements starting from the {@code first} exclusively.
     *          If none found an empty set will be returned. {@link Collections#emptySortedSet()}.
     */
    CompletableFuture<SortedSet<Whitelistable>> findAll(int limit);

    /**
     * Finds {@code limit} elements starting from the {@code first} exclusively.
     *
     * @param first an element to start searching with. This element will not be included
     *              in a resulting {@link SortedSet}.
     * @param limit maximum number of elements to search for.
     *
     * @return {@code limit} number of elements starting from the {@code first} exclusively.
     *          If none found an empty set will be returned. {@link Collections#emptySortedSet()}.
     */
    CompletableFuture<SortedSet<Whitelistable>> findAll(final Whitelistable first, int limit);

    CompletableFuture<Optional<Whitelistable>> findFirst();

    CompletableFuture<Optional<Whitelistable>> findLast();

    CompletableFuture<Integer> size();

    CompletableFuture<Optional<Whitelistable>> find(final UUID uuid);

    CompletableFuture<Whitelistable> add(final UUID uuid, final Date expiresAt);

    CompletableFuture<Boolean> remove(final Whitelistable whitelistable);
}
