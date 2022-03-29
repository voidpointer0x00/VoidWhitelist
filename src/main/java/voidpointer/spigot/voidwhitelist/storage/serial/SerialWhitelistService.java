/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.storage.serial;

import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.CachedWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Set;

public final class SerialWhitelistService extends CachedWhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.ser";

    @AutowiredLocale private static LocaleLog log;
    private final File dataFolder;

    public SerialWhitelistService(final @NonNull File dataFolder) {
        this.dataFolder = dataFolder;
        load();
        CacheBuilder.newBuilder().weakValues();
    }

    @Override protected void saveWhitelist() {
        try {
            File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
            if (whitelistFile.createNewFile())
                log.info("Created {0}", WHITELIST_FILE_NAME);

            ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(whitelistFile));
            objOut.writeObject(StorageVersion.CURRENT.toString());
            objOut.writeObject(this.getCachedWhitelist());
            objOut.close();
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
            if (deserializeVersion(oin.readObject()) != StorageVersion.CURRENT) {
                log.severe("Can't load whitelist: serial storage does not support different versions.");
                return;
            }
            getCachedWhitelist().addAll(deserializeWhitelist(oin.readObject()));
        } catch (IOException | ClassNotFoundException | ClassCastException deserializationException) {
            log.severe("Cannot deserialize whitelist object from file.", deserializationException);
            return;
        }
    }

    private StorageVersion deserializeVersion(final Object obj) throws ClassCastException {
        if (!(obj instanceof String)) {
            throw new ClassCastException(String.format("Invalid type of storage version object " +
                    "(supposed to be String, but loaded %s)", obj.getClass().getName()));
        }
        String versionStr = (String) obj;
        for (StorageVersion storageVersion : StorageVersion.values())
            if (storageVersion.toString().equals(versionStr))
                return storageVersion;
        throw new ClassCastException("Unknown version: " + versionStr);
    }

    private Set<Whitelistable> deserializeWhitelist(final Object obj) {
        if (!(obj instanceof Set<?>)) {
            throw new ClassCastException(String.format("Invalid type of whitelist object " +
                    "(supposed to be Set, but loaded %s)", obj.getClass().getName()));
        }
        Set<?> supposedlyWhitelistableSet = (Set<?>) obj;
        if (supposedlyWhitelistableSet.isEmpty())
            return Collections.EMPTY_SET;
        Object supposedlyWhitelistable = supposedlyWhitelistableSet.iterator().next();
        if (supposedlyWhitelistable instanceof Whitelistable)
            return (Set<Whitelistable>) supposedlyWhitelistableSet;

        throw new ClassCastException(String.format("Whitelist object contains unknown elements. " +
                "(supposed to be Whitelistable, but loaded %s)", supposedlyWhitelistable.getClass().getName()));
    }
}
