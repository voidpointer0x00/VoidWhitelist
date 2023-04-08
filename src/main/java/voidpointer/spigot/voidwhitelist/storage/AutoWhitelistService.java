package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AutoWhitelistService extends WhitelistService {
    CompletableFuture<Optional<AutoWhitelistNumber>> getAutoWhitelistNumberOf(final UUID uniqueId);

    CompletableFuture<Optional<Whitelistable>> add(final UUID uuid, final String name, final Date expiresAt,
                                                   final int timesAutoWhitelisted);
}
