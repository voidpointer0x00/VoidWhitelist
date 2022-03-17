package voidpointer.spigot.voidwhitelist.storage.serial;

import lombok.NonNull;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.CachedWhitelistService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.logging.Logger;

public final class SerialWhitelistService extends CachedWhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.ser";

    @NonNull private final Logger log;
    @NonNull private final File dataFolder;

    public SerialWhitelistService(final Logger log, final File dataFolder) {
        this.log = log;
        this.dataFolder = dataFolder;
        load();
    }

    @Override protected void saveWhitelist() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException ioException) {
                log.severe("Cannot save whitelist.");
                ioException.printStackTrace();
                return;
            }
        }

        try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(whitelistFile))) {
            objOut.writeObject(this.getCachedWhitelist());
        } catch (IOException ioException) {
            log.severe("Cannot save whitelist.");
            ioException.printStackTrace();
        }
    }

    protected void load() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists())
            return; // nothing to load

        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(whitelistFile))) {
            Object readObject = oin.readObject();
            if (!(readObject instanceof Set<?>)) {
                throw new ClassCastException(String.format("Invalid type of whitelist object" +
                        "(supposed to be Set, but loaded %s)", readObject.getClass().getName()));
            }
            getCachedWhitelist().addAll((Set<Whitelistable>) readObject);
        } catch (IOException | ClassNotFoundException | ClassCastException deserializationException) {
            log.severe("Cannot deserialize whitelist object from file.");
            deserializationException.printStackTrace();
            return;
        }
    }
}
