package voidpointer.spigot.voidwhitelist.uuid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

public final class UUIDFetcher {
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";

    private UUIDFetcher() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the UUID of the searched player.
     *
     * @param name The name of the player.
     * @return The UUID of the given player.
     */
    public static UUID getUUID(final String name) throws IOException {
        String output = callURL(UUID_URL + name);
        StringBuilder result = new StringBuilder();
        readData(output, result);
        String u = result.toString();
        StringBuilder uuid = new StringBuilder();
        for (int i = 0; i <= 31; i++) {
            uuid.append(u.charAt(i));
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                uuid.append('-');
            }
        }
        return UUID.fromString(uuid.toString());
    }

    private static void readData(final String toRead, final StringBuilder result) {
        /* toRead is a string "{"name":"_voidpointer","id":"c55a15b5896f4c099c0775ad36572aad"}"
         * so this method reads the "id" property from the end of the string */
        for (int i = toRead.length() - 3; i >= 0; i--) {
            if (toRead.charAt(i) != '"') {
                result.insert(0, toRead.charAt(i));
            } else {
                break;
            }
        }
    }

    private static String callURL(final String urlStr) throws IOException {
        StringBuilder response = new StringBuilder();
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection == null)
            throw new IOException("Couldn't connect to Mojang API");
        if (connection.getResponseCode() == 204)
            throw new IllegalArgumentException("Player not found");
        if (connection.getInputStream() == null)
            throw new IOException("No InputStream in connection");

        connection.setReadTimeout(60 * 1000);
        InputStreamReader in = new InputStreamReader(connection.getInputStream(),
                Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(in);
        int cp;
        while ((cp = bufferedReader.read()) != -1) {
            response.append((char) cp);
        }
        bufferedReader.close();
        in.close();

        return response.toString();
    }

}