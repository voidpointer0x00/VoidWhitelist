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

import voidpointer.spigot.voidwhitelist.command.arg.ArgOption;
import voidpointer.spigot.voidwhitelist.command.arg.UuidOptions;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class DefaultUUIDFetcher {
    private static Function<String, CompletableFuture<Optional<UUID>>> defaultMethod;

    public static void updateMode(final boolean isOnlineMode) {
        defaultMethod = isOnlineMode
                ? OnlineUUIDFetcher::getUUID
                : OfflineUUIDFetcher::getUUID;
    }

    public static CompletableFuture<Optional<UUID>> getUUID(final String name) {
        return defaultMethod.apply(name);
    }

    public static UUIDFetcher of(final Collection<ArgOption> options) {
        if (options.contains(UuidOptions.ONLINE))
            return OnlineUUIDFetcher::getUUID;
        else if (options.contains(UuidOptions.OFFLINE))
            return OfflineUUIDFetcher::getUUID;
        return DefaultUUIDFetcher::getUUID;
    }
}
