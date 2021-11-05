package voidpointer.spigot.voidwhitelist.storage.serial;

import lombok.NonNull;
import voidpointer.spigot.voidwhitelist.VwPlayer;
import voidpointer.spigot.voidwhitelist.storage.NotWhitelistedException;
import voidpointer.spigot.voidwhitelist.storage.SimpleVwPlayer;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class SerialWhitelistService implements WhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.ser";

    private Map<String, VwPlayer> whitelist = new TreeMap<>();
    @NonNull private final Logger log;
    @NonNull private final File dataFolder;

    public SerialWhitelistService(final Logger log, final File dataFolder) {
        this.log = log;
        this.dataFolder = dataFolder;
        load();
    }

    @Override
    public CompletableFuture<VwPlayer> findVwPlayer(final String name) {
        return CompletableFuture.supplyAsync(() -> whitelist.get(name));
    }

    @Override public CompletableFuture<List<String>> getAllWhitelistedNicknames() {
        return CompletableFuture.supplyAsync(() -> whitelist.entrySet().stream()
                .filter(entry -> entry.getValue().isAllowedToJoin())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList()));
    }

    @Override public CompletableFuture<VwPlayer> addToWhitelist(final String nickname) {
        return addToWhitelist(nickname, VwPlayer.NEVER_EXPIRES);
    }

    @Override public CompletableFuture<VwPlayer> addToWhitelist(final String nickname, final Date expiresAt) {
        return CompletableFuture.supplyAsync(() -> {
            VwPlayer vwPlayer = new SimpleVwPlayer(nickname, expiresAt);
            whitelist.put(nickname, vwPlayer);
            saveWhitelist();
            return vwPlayer;
        });
    }

    @Override
    public CompletableFuture<Boolean> removeFromWhitelist(final VwPlayer vwPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            whitelist.remove(vwPlayer.getName());
            saveWhitelist();
            return true;
        });
    }

    private void load() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists())
            return; // nothing to load

        Collection<VwPlayer> whitelistPlayers;
        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(whitelistFile))) {
            Object deserializedObject = oin.readObject();
            if (!(deserializedObject instanceof Map<?, ?>))
                throw new ClassCastException("Deserialized object isn't whitelist map.");
            whitelistPlayers = (Collection<VwPlayer>) deserializedObject;
        } catch (IOException | ClassNotFoundException | ClassCastException deserializationException) {
            log.severe("Cannot deserialize whitelist storage object from file.");
            deserializationException.printStackTrace();
            return;
        }

        whitelistPlayers.forEach(vwPlayer -> whitelist.put(vwPlayer.getName(), vwPlayer));
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
