package voidpointer.spigot.voidwhitelist.command.whitelist;

import com.j256.ormlite.dao.CloseableWrappedIterable;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.TimesAutoWhitelistedModel;
import voidpointer.spigot.voidwhitelist.storage.json.JsonAutoWhitelist;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelist;

import java.io.File;

import static java.lang.System.currentTimeMillis;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public class ExportCommand extends Command {
    public static final String NAME = "export-db";

    @AutowiredLocale private static LocaleLog localeLog;
    @Autowired(mapId="plugin") private static Plugin plugin;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;

    public ExportCommand() {
        super(NAME);
    }

    @Override public void execute(final Args args) {
        if (!(whitelistService instanceof OrmliteWhitelistService database)) {
            localeLog.localize(EXPORT_ONLY_FROM_DATABASE).send(args.getSender());
            return;
        }
        localeLog.localize(EXPORT_GATHERING).send(args.getSender());
        /* whitelist export */
        database.getTotalCountOfWhitelist().thenAcceptAsync(optionalTotalWhitelist -> {
            final long total = optionalTotalWhitelist.orElse(0L);
            localeLog.localize(WHITELIST_EXPORT_PROCESSING).set("total", total).send(args.getSender());
            database.findAll().thenAccept(allWhitelistable -> exportWhitelist(allWhitelistable, args.getSender()));
        });
        /* auto-whitelist export */
        database.getTotalCountOfAutoWhitelist().thenAcceptAsync(optionalTotalAutoWhitelist -> {
            final long total = optionalTotalAutoWhitelist.orElse(0L);
            localeLog.localize(AUTO_WHITELIST_EXPORT_PROCESSING).set("total", total).send(args.getSender());
            database.findAllAuto().thenAccept(allAuto -> exportAuto(allAuto, args.getSender()));
        });
    }

    private void exportWhitelist(final CloseableWrappedIterable<? extends Whitelistable> allWhitelistable,
                                 final CommandSender sender) {
        final long start = currentTimeMillis();
        final JsonWhitelist jsonWhitelist = new JsonWhitelist();
        try (allWhitelistable) {
            for (final Whitelistable whitelistable : allWhitelistable)
                jsonWhitelist.add(whitelistable);
        } catch (final Exception exception) {
            localeLog.localize(WHITELIST_EXPORT_FAILURE).send(sender);
            localeLog.warn("Unable to export whitelist: {0}", exception.getMessage());
        }

        if (jsonWhitelist.save(new File(plugin.getDataFolder(), "whitelist-export-" + currentTimeMillis() + ".json"))) {
            localeLog.localize(WHITELIST_EXPORT_FINISHED)
                    .set("ms-spent", currentTimeMillis() - start)
                    .send(sender);
        } else {
            localeLog.localize(WHITELIST_EXPORT_FAILURE).send(sender);
        }
    }

    private void exportAuto(final CloseableWrappedIterable<TimesAutoWhitelistedModel> allAuto,
                            final CommandSender sender) {
        final long start = currentTimeMillis();
        final JsonAutoWhitelist jsonAutoWhitelist = new JsonAutoWhitelist();
        try (allAuto) {
            for (final TimesAutoWhitelistedModel timesAutoWhitelisted : allAuto)
                jsonAutoWhitelist.add(timesAutoWhitelisted);
        } catch (final Exception exception) {
            localeLog.localize(AUTO_WHITELIST_EXPORT_FAILURE).send(sender);
            localeLog.warn("Unable to export auto-whitelist: {0}", exception.getMessage());
        }

        if (jsonAutoWhitelist.save(new File(plugin.getDataFolder(),
                "auto-whitelist-export-" + currentTimeMillis() + ".json"))) {
            localeLog.localize(AUTO_WHITELIST_EXPORT_FINISHED)
                    .set("ms-spent", currentTimeMillis() - start)
                    .send(sender);
        } else {
            localeLog.localize(AUTO_WHITELIST_EXPORT_FAILURE).send(sender);
        }
    }
}
