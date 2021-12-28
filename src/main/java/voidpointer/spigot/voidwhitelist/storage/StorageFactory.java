package voidpointer.spigot.voidwhitelist.storage;

import com.google.common.io.Files;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.voidwhitelist.config.StorageVersion;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.storage.json.JsonNameWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialNameWhitelistService;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class StorageFactory {
    @NonNull private final File dataFolder;

    public WhitelistService loadStorage(final Logger log, final WhitelistConfig whitelistConfig) {
        switch (whitelistConfig.getStorageMethod()) {
            case JSON: {
                if (whitelistConfig.getStorageVersion() != StorageVersion.CURRENT) {
                    makeOldStorageBackup(log, JsonNameWhitelistService.WHITELIST_FILE_NAME);
                    whitelistConfig.setStorageVersion(StorageVersion.CURRENT);
                }
                return new JsonNameWhitelistService(log, dataFolder);
            }
            case SERIAL: {
                if (whitelistConfig.getStorageVersion() != StorageVersion.CURRENT) {
                    makeOldStorageBackup(log, SerialNameWhitelistService.WHITELIST_FILE_NAME);
                    whitelistConfig.setStorageVersion(StorageVersion.CURRENT);
                }
                return new SerialNameWhitelistService(log, dataFolder);
            }
            default:
                throw new UnsupportedOperationException("Unknown storage method: "
                                                        + whitelistConfig.getStorageMethod());
        }
    }

    private void makeOldStorageBackup(final Logger log, final String oldStorageFilename) {
        File oldStorageFile = new File(dataFolder, oldStorageFilename);
        if (!oldStorageFile.exists())
            return;
        File backupFile = new File(dataFolder, oldStorageFilename + ".old." + System.currentTimeMillis());
        try {
            Files.move(oldStorageFile, backupFile);
            log.info("Made backup file " + backupFile.getName() + " as storage versions did not match");
        } catch (IOException ioException) {
            log.log(Level.SEVERE, "Unable to make backup for old storage", ioException);
        }
    }
}
