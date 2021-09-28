package voidpointer.spigot.voidwhitelist.storage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class StorageFactory {
    @NonNull private final File dataFolder;

    public WhitelistService loadStorage(final Logger log, final StorageMethod storageMethod) {
        switch (storageMethod) {
            case SERIAL:
                return new SerialWhitelistService(log, dataFolder);
            case JSON:
                return new JsonWhitelistService(log, dataFolder);
            default:
                throw new UnsupportedOperationException("Unknown storage method: " + storageMethod);
        }
    }
}
