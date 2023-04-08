package voidpointer.spigot.voidwhitelist.command;

import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelist;

import java.io.File;

import static java.lang.System.currentTimeMillis;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public class ExportCommand extends Command {
    public static final String NAME = "export-db";
    public static final String PERMISSION = "whitelist.export";

    @AutowiredLocale private static LocaleLog localeLog;
    @Autowired(mapId="plugin") private static Plugin plugin;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;

    public ExportCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        if (!(whitelistService instanceof OrmliteWhitelistService)) {
            localeLog.localize(EXPORT_ONLY_FROM_DATABASE).send(args.getSender());
            return;
        }
        OrmliteWhitelistService database = (OrmliteWhitelistService) whitelistService;
        localeLog.localize(EXPORT_GATHERING).send(args.getSender());
        database.findAll().thenAcceptAsync(allWhitelistable -> {
            final long start = currentTimeMillis();
            JsonWhitelist jsonWhitelist = new JsonWhitelist();
            long nGathered = 0;
            for (final Whitelistable whitelistable : allWhitelistable) {
                jsonWhitelist.add(whitelistable);
                nGathered++;
            }
            localeLog.localize(EXPORT_PROCESSING).set("gathered", nGathered).send(args.getSender());

            if (jsonWhitelist.save(newExportFile())) {
                localeLog.localize(EXPORT_FINISHED)
                        .set("ms-spent", currentTimeMillis() - start)
                        .send(args.getSender());
            } else {
                localeLog.localize(EXPORT_FAILURE).send(args.getSender());
            }
        });
    }

    private File newExportFile() {
        return new File(plugin.getDataFolder(), "export-" + currentTimeMillis() + ".json");
    }
}
