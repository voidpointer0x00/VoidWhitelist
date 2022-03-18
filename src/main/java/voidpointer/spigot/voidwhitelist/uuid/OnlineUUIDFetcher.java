package voidpointer.spigot.voidwhitelist.uuid;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RequiredArgsConstructor
public final class OnlineUUIDFetcher implements UUIDFetcher {
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private final Logger log;
    private final Cache<String, UUID> uniqueIdCache = CacheBuilder.newBuilder()
            .expireAfterAccess(6L, TimeUnit.HOURS)
            .build();

    /**
     * Returns the UUID of the searched player.
     *
     * @param name The name of the player.
     * @return The UUID of the given player.
     */
    public UUID getUUID(final String name) {
        UUID uniqueId = uniqueIdCache.getIfPresent(name);
        if (uniqueId != null)
            return uniqueId;

        String response = callURL(UUID_URL + name);
        if (response == null)
            return null;

        String uniqueIdStr = readUniqueIdFromResponse(response);
        uniqueId = UUID.fromString(uniqueIdStr.toString());
        uniqueIdCache.put(name, uniqueId);
        return uniqueId;
    }

    private String callURL(final String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException malformedURLException) {
            log.severe("Invalid URL: " + urlStr);
            return null;
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException ioException) {
            log.severe("Couldn't open HTTP connection: " + ioException.getMessage());
            return null;
        }
        try {
            if (connection.getResponseCode() == 204) {
                log.warning("Requested player not found (url: " + urlStr + ")");
                return null;
            } else if (connection.getInputStream() == null) {
                log.severe("No InputStream in connection");
                return null;
            }
        } catch (IOException ioException) {
            log.severe(ioException.getMessage());
            return null;
        }
        connection.setReadTimeout(60 * 1000 /* 60 sec */);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream in = connection.getInputStream()) {
            byte[] buffer = new byte[1024];
            for (int len; (len = in.read(buffer)) != -1; )
                out.write(buffer, 0, len);
        } catch (IOException ioException) {
            log.severe("Unable to fetch UUID: I/O failure");
            ioException.printStackTrace();
            return null;
        }
        return out.toString();
    }

    private String readUniqueIdFromResponse(final String toRead) {
        final StringBuilder result = new StringBuilder();
        /* toRead is a string of format "{"name":"_voidpointer","id":"c55a15b5896f4c099c0775ad36572aad"}"
         * so this method reads the "id" property from the end of that string
         * and additionally adds '-' char where it's needed to make the id look like
         * c55a15b5-896f-4c09-9c07-75ad36572aad */
        for (int i = toRead.length() - 3, j = 31; i >= 0; i--, j--) {
            if (toRead.charAt(i) != '"') {
                if ((j == 19) || (j == 15) || (j == 11) || (j == 7))
                    result.insert(0, '-');
                result.insert(0, toRead.charAt(i));
            } else {
                break;
            }
        }
        return result.toString();
    }
}