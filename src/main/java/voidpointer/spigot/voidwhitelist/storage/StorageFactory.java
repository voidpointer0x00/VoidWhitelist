package voidpointer.spigot.voidwhitelist.storage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;

import java.io.File;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class StorageFactory {
    @NonNull private final File dataFolder;

    public WhitelistService loadStorage(final Logger log, final WhitelistConfig whitelistConfig) {
        switch (whitelistConfig.getStorageMethod()) {
            case JSON:
                return new JsonWhitelistService(log, dataFolder);
            case SERIAL:
                return new SerialWhitelistService(log, dataFolder);
            default:
                throw new UnsupportedOperationException("Unknown storage method: "
                                                        + whitelistConfig.getStorageMethod());
        }
    }
}
