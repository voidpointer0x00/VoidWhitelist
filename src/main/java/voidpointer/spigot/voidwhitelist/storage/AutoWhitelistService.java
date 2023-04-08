package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AutoWhitelistService extends WhitelistService {
    CompletableFuture<Optional<AutoWhitelistNumber>> getAutoWhitelistNumberOf(final UUID uniqueId);
}
