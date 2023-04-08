package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.AutoWhitelistNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AutoWhitelistService extends WhitelistService {
    CompletableFuture<Optional<AutoWhitelistNumber>> getAutoWhitelistNumberOf(final Whitelistable whitelistable);
}
