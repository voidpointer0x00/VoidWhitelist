package voidpointer.spigot.voidwhitelist.storage.serial;

import lombok.NonNull;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.WhitelistableName;
import voidpointer.spigot.voidwhitelist.storage.SimpleWhitelistableName;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class SerialWhitelistService implements WhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.ser";

    private final Map<String, WhitelistableName> whitelistNames = new TreeMap<>();
    @NonNull private final Logger log;
    @NonNull private final File dataFolder;

    public SerialWhitelistService(final Logger log, final File dataFolder) {
        this.log = log;
        this.dataFolder = dataFolder;
        load();
    }

    @Override
    public CompletableFuture<WhitelistableName> findNick(final String name) {
        return CompletableFuture.supplyAsync(() -> whitelistNames.get(name));
    }

    @Override public CompletableFuture<List<String>> getAllWhitelistedNicknames() {
        return CompletableFuture.supplyAsync(() -> whitelistNames.entrySet().stream()
                .filter(entry -> entry.getValue().isAllowedToJoin())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }

    @Override public CompletableFuture<WhitelistableName> addNickToWhitelist(final String name) {
        return addNickToWhitelist(name, Whitelistable.NEVER_EXPIRES);
    }

    @Override public CompletableFuture<WhitelistableName> addNickToWhitelist(final String name, final Date expiresAt) {
        return CompletableFuture.supplyAsync(() -> {
            WhitelistableName whitelistableName = new SimpleWhitelistableName(name, expiresAt);
            whitelistNames.put(name, whitelistableName);
            saveWhitelist();
            return whitelistableName;
        });
    }

    @Override
    public CompletableFuture<Boolean> removeFromWhitelist(final WhitelistableName name) {
        return CompletableFuture.supplyAsync(() -> {
            whitelistNames.remove(name.toString());
            saveWhitelist();
            return true;
        });
    }

    private void load() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists())
            return; // nothing to load

        Collection<WhitelistableName> whitelistNames;
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(whitelistFile))) {
            Object deserializedObject = oin.readObject();
            if (!(deserializedObject instanceof Collection<?>))
                throw new ClassCastException("Deserialized object isn't whitelist map.");
            whitelistNames = (Collection<WhitelistableName>) deserializedObject;
        } catch (IOException | ClassNotFoundException | ClassCastException deserializationException) {
            log.severe("Cannot deserialize whitelist storage object from file.");
            deserializationException.printStackTrace();
            return;
        }

        // insert loaded names into the whitelist map
        whitelistNames.forEach(name -> this.whitelistNames.put(name.toString(), name));
    }

    private void saveWhitelist() {
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
            objOut.writeObject(whitelistNames.values());
        } catch (IOException ioException) {
            log.severe("Cannot save whitelist.");
            ioException.printStackTrace();
        }
    }
}
