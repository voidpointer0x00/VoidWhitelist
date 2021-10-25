package voidpointer.spigot.voidwhitelist.storage;

import com.sun.org.apache.xerces.internal.dom.CoreDOMImplementationImpl;
import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface WhitelistService {
    CompletableFuture<VwPlayer> findVwPlayer(final String name);

    CompletableFuture<List<String>> getAllWhitelistedNicknames();

    CompletableFuture<VwPlayer> addToWhitelist(final String name);

    CompletableFuture<VwPlayer> addToWhitelist(final String name, final Date expiresAt);

    CompletableFuture<Boolean> removeFromWhitelist(final VwPlayer vwPlayer);
}
