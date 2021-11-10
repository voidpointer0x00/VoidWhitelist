package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {
    CompletableFuture<VwPlayer> findVwPlayer(final String name);

    CompletableFuture<List<String>> getAllWhitelistedNicknames();

    CompletableFuture<VwPlayer> addToWhitelist(final String name);

    CompletableFuture<VwPlayer> addToWhitelist(final String name, final Date expiresAt);

    CompletableFuture<Boolean> removeFromWhitelist(final VwPlayer vwPlayer);
}
