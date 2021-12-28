package voidpointer.spigot.voidwhitelist.storage.serial;

import lombok.NonNull;
import org.bukkit.entity.Player;
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
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Collections.synchronizedMap;

public final class SerialNameWhitelistService implements WhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.ser";

    private final Map<String, Whitelistable> whitelist = synchronizedMap(new TreeMap<>());
    @NonNull private final Logger log;
    @NonNull private final File dataFolder;

    public SerialNameWhitelistService(final Logger log, final File dataFolder) {
        this.log = log;
        this.dataFolder = dataFolder;
        load();
    }

    @Override public CompletableFuture<Optional<Whitelistable>> find(final Player player) {
        return CompletableFuture.supplyAsync(() -> Optional.of(whitelist.get(player.getName())));
    }

    @Override public CompletableFuture<List<String>> getAllWhitelistedNames() {
        return CompletableFuture.supplyAsync(() -> whitelist.entrySet().stream()
                .filter(entry -> entry.getValue().isAllowedToJoin())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }

    @Override public CompletableFuture<Whitelistable> add(final Player player) {
        return add(player, Whitelistable.NEVER_EXPIRES);
    }

    @Override public CompletableFuture<Whitelistable> add(final Player player, final Date expiresAt) {
        return CompletableFuture.supplyAsync(() -> {
            WhitelistableName whitelistableName = new SimpleWhitelistableName(player.getName(), expiresAt);
            whitelist.put(player.getName(), whitelistableName);
            saveWhitelist();
            return whitelistableName;
        });
    }

    @Override public CompletableFuture<Boolean> remove(final Whitelistable whitelistable) {
        if (!(whitelistable instanceof WhitelistableName))
            throw new IllegalArgumentException("Expected WhitelistableName, but given "
                    + whitelistable.getClass().getSimpleName());

        return CompletableFuture.supplyAsync(() -> {
            whitelist.remove(whitelistable.toString());
            saveWhitelist();
            return true;
        });
    }

    private void load() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists())
            return; // nothing to load

        Collection<Whitelistable> whitelist;
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(whitelistFile))) {
            Object deserializedObject = oin.readObject();
            if (!(deserializedObject instanceof Collection<?>))
                throw new ClassCastException("Deserialized object isn't whitelist map.");
            whitelist = (Collection<Whitelistable>) deserializedObject;
        } catch (IOException | ClassNotFoundException | ClassCastException deserializationException) {
            log.severe("Cannot deserialize whitelist storage object from file.");
            deserializationException.printStackTrace();
            return;
        }

        // insert loaded names into the whitelist map
        whitelist.forEach(name -> this.whitelist.put(name.toString(), name));
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
            objOut.writeObject(whitelist.values());
        } catch (IOException ioException) {
            log.severe("Cannot save whitelist.");
            ioException.printStackTrace();
        }
    }
}
