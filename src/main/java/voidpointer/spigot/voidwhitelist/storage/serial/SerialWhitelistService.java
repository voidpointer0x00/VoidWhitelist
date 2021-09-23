package voidpointer.spigot.voidwhitelist.storage.serial;

import lombok.NonNull;
import voidpointer.spigot.voidwhitelist.storage.NotWhitelistedException;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class SerialWhitelistService implements WhitelistService {
    public static final String WHITELIST_FILE_NAME = "whitelist.ser";

    private Map<String, Date> whitelist = new TreeMap<>();
    @NonNull private final File dataFolder;

    public SerialWhitelistService(final File dataFolder) {
        this.dataFolder = dataFolder;
        load();
    }

    @Override public boolean isWhitelisted(final String nickname) {
        if (!whitelist.containsKey(nickname))
            return false;

        Date expiresAt = whitelist.get(nickname);
        if (NEVER_EXPIRES == expiresAt)
            return true;
        return whitelist.get(nickname).after(Date.from(Instant.now()));
    }

    @Override public Date getExpiresAt(final String nickname) throws NotWhitelistedException {
        if (!whitelist.containsKey(nickname)) {
            throw new NotWhitelistedException("Trying to getExpiresAt() of a not whitelisted player \"" + nickname + "\"");
        }
        return whitelist.get(nickname);
    }

    @Override public List<String> getWhitelistedNicknames() {
        Date currentDate = Date.from(Instant.now());
        return whitelist.entrySet().stream()
                .filter(entry -> (NEVER_EXPIRES == entry.getValue()) || entry.getValue().after(currentDate))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }

    @Override public boolean addToWhitelist(final String nickname) {
        return addToWhitelist(nickname, NEVER_EXPIRES);
    }

    @Override public boolean addToWhitelist(final String nickname, final Date expiresAt) {
        whitelist.put(nickname, expiresAt);
        saveWhitelist();
        return true;
    }

    @Override public boolean removeFromWhitelist(final String nickname) {
        whitelist.remove(nickname);
        saveWhitelist();
        return true;
    }

    private void load() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists())
            return; // nothing to load

        try (ObjectInputStream oin = new ObjectInputStream(new FileInputStream(whitelistFile))) {
            Object deserializedObject = oin.readObject();
            if (!(deserializedObject instanceof Map<?, ?>))
                throw new ClassCastException("Deserialized object isn't whitelist map.");
            whitelist = (Map<String, Date>) deserializedObject;
        } catch (IOException | ClassNotFoundException | ClassCastException deserializationException) {
            System.err.println("Cannot deserialize whitelist storage object from file.");
            deserializationException.printStackTrace();
        }
    }

    private void saveWhitelist() {
        File whitelistFile = new File(dataFolder, WHITELIST_FILE_NAME);
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException ioException) {
                System.err.println("Cannot save whitelist.");
                ioException.printStackTrace();
                return;
            }
        }

        try (ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(whitelistFile))) {
            objOut.writeObject(whitelist);
        } catch (IOException ioException) {
            System.err.println("Cannot save whitelist.");
            ioException.printStackTrace();
        }
    }
}
