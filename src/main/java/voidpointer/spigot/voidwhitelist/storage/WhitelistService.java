package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.WhitelistableName;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {
    CompletableFuture<WhitelistableName> findByName(final String name);

    CompletableFuture<List<String>> getAllWhitelistedNames();

    CompletableFuture<WhitelistableName> addName(final String name);

    CompletableFuture<WhitelistableName> addName(final String name, final Date expiresAt);

    CompletableFuture<Boolean> removeName(final WhitelistableName whitelistableName);
}
