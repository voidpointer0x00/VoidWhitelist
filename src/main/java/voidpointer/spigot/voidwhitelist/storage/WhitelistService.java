package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {
    CompletableFuture<Optional<Whitelistable>> find(final UUID uuid);

    CompletableFuture<Whitelistable> add(final UUID uuid);

    CompletableFuture<Whitelistable> add(final UUID uuid, final Date expiresAt);

    CompletableFuture<Boolean> remove(final Whitelistable whitelistable);
}
