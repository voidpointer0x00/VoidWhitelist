package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

abstract class JsonStorage {
    protected static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateJsonSerializer())
            .registerTypeAdapter(Date.class, new DateJsonDeserializer())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableInstanceCreator())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableJsonSerializer())
            .registerTypeAdapter(Whitelistable.class, new WhitelistableJsonDeserializer())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    @AutowiredLocale protected static LocaleLog log;

    protected static boolean save(final JsonElement whitelistAndMeta, final @NonNull File destination) {
        try {
            Files.asCharSink(destination, UTF_8).write(gson.toJson(whitelistAndMeta));
            return true;
        } catch (final IOException ioException) {
            log.warn("Couldn't save whitelist due to I/O error: {0}", ioException.getMessage());
            return false;
        } catch (final Exception exception) {
            log.severe("Unknown exception while saving whitelist", exception);
            return false;
        }
    }

    protected static String fileToString(final File file) {
        try {
            return Files.asCharSource(file, UTF_8).read();
        } catch (final IOException e) {
            log.warn("Unable to read {0} contents: {1}", file.getName(), e.getMessage());
            return null;
        }
    }
}
