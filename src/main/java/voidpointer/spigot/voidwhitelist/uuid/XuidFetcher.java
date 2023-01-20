package voidpointer.spigot.voidwhitelist.uuid;

import org.bukkit.Bukkit;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class XuidFetcher {
    public static boolean isFloodgateSupported() {
        return Bukkit.getPluginManager().getPlugin("floodgate") != null;
    }

    public static CompletableFuture<Optional<UUID>> fetch(final String gameTag) {
        if (!isFloodgateSupported())
            return CompletableFuture.completedFuture(Optional.empty());
        return FloodgateApi.getInstance().getUuidFor(gameTag).thenApplyAsync(Optional::ofNullable);
    }
}
