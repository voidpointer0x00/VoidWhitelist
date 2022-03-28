package voidpointer.spigot.voidwhitelist.uuid;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UUIDFetcher {
    CompletableFuture<Optional<UUID>> getUUID(final String name);
}
