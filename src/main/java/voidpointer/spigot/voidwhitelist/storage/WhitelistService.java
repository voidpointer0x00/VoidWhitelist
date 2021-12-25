package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.WhitelistableName;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {
    CompletableFuture<WhitelistableName> findNick(final String name);

    CompletableFuture<List<String>> getAllWhitelistedNicknames();

    CompletableFuture<WhitelistableName> addToWhitelist(final String name);

    CompletableFuture<WhitelistableName> addToWhitelist(final String name, final Date expiresAt);

    CompletableFuture<Boolean> removeFromWhitelist(final WhitelistableName whitelistableName);
}
