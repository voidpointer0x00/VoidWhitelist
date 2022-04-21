package voidpointer.spigot.voidwhitelist.command;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteWhitelistService;

import java.io.File;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;
import static voidpointer.spigot.voidwhitelist.storage.json.WhitelistableJsonSerializer.serialize;

public class ExportCommand extends Command {
    public static final String NAME = "export-db";
    public static final String PERMISSION = "whitelist.export";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @AutowiredLocale private static LocaleLog localeLog;
    @Autowired(mapId="plugin") private static Plugin plugin;
    @Autowired private static WhitelistService whitelistService;

    public ExportCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        if (!(whitelistService instanceof OrmliteWhitelistService)) {
            localeLog.localize(EXPORT_ONLY_FROM_DATABASE);
            return;
        }
        OrmliteWhitelistService database = (OrmliteWhitelistService) whitelistService;
        localeLog.localize(EXPORT_GATHERING).send(args.getSender());
        database.findAll().thenAcceptAsync(allWhitelistable -> {
            final long start = currentTimeMillis();
            // TODO: create JsonWhitelist class
            //  that will contain JsonObject with meta info and whitelist array
            //  and will provide an API to those objects
            JsonObject jsonWhitelistWithMeta = new JsonObject();
            jsonWhitelistWithMeta.add("version", new JsonPrimitive(StorageVersion.CURRENT.toString()));

            long nGathered = 0;
            JsonArray whitelistArray = new JsonArray();
            for (final Whitelistable whitelistable : allWhitelistable) {
                whitelistArray.add(serialize(whitelistable));
                nGathered++;
            }
            jsonWhitelistWithMeta.add("whitelist", whitelistArray);
            localeLog.localize(EXPORT_PROCESSING).set("gathered", nGathered).send(args.getSender());

            if (saveExportedWhitelist(jsonWhitelistWithMeta)) {
                localeLog.localize(EXPORT_FINISHED)
                        .set("ms-spent", currentTimeMillis() - start)
                        .send(args.getSender());
            } else {
                localeLog.localize(EXPORT_FAILURE).send(args.getSender());
            }
        });
    }

    private boolean saveExportedWhitelist(final JsonObject whitelistObject) {
        try {
            File exportFile = new File(plugin.getDataFolder(), "export-" + currentTimeMillis() + ".json");
            Files.asCharSink(exportFile, UTF_8).write(gson.toJson(whitelistObject));
            return true;
        } catch (final IOException ioException) {
            localeLog.warn("Couldn't' export whitelist", ioException);
            return false;
        } catch (final Exception exception) {
            localeLog.severe("Unknown exception while saving exported whitelist", exception);
            return false;
        }
    }
}
