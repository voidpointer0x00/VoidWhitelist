package voidpointer.spigot.voidwhitelist.storage;

import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {
    CompletableFuture<Optional<Whitelistable>> find(final Player player);

    CompletableFuture<List<String>> getAllWhitelistedNames();

    CompletableFuture<Whitelistable> add(final Player player);

    CompletableFuture<Whitelistable> add(final Player player, final Date expiresAt);

    CompletableFuture<Boolean> remove(final Whitelistable whitelistable);
}
