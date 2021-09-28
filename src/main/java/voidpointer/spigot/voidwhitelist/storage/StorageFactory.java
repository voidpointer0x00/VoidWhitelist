package voidpointer.spigot.voidwhitelist.storage;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;

import java.io.File;
import java.util.Arrays;

@RequiredArgsConstructor
public final class StorageFactory {
    public static final StorageMethod DEFAULT_METHOD = StorageMethod.JSON;

    @NonNull private final File dataFolder;

    public WhitelistService loadStorage(final StorageMethod storageMethod) {
        switch (storageMethod) {
            case SERIAL:
                return new SerialWhitelistService(dataFolder);
            case JSON:
                return new JsonWhitelistService(dataFolder);
            default:
                throw new UnsupportedOperationException("Unknown storage method: " + storageMethod);
        }
    }
}
