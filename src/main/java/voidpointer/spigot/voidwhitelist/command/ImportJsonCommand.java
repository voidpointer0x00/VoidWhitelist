/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.command;

import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.TimesAutoWhitelistedNumber;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.command.arg.ImportOptions;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static voidpointer.spigot.voidwhitelist.command.arg.ImportOptions.REPLACE;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;
import static voidpointer.spigot.voidwhitelist.storage.StorageMethod.JSON;
import static voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService.WHITELIST_FILE_NAME;

public class ImportJsonCommand extends Command {
    public static final String NAME = "import-json";
    public static final String PERMISSION = "whitelist.import";

    @AutowiredLocale private static Locale locale;
    @Autowired private static EventManager eventManager;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;
    @Autowired private static StorageFactory storageFactory;
    @Autowired(mapId="plugin")
    private static Plugin plugin;

    public ImportJsonCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
        super.addOptions(ImportOptions.values());
    }

    @Override public void execute(final Args args) {
        if (!(whitelistService instanceof OrmliteWhitelistService)) {
            locale.localize(IMPORT_ONLY_TO_DATABASE).send(args.getSender());
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> importJson(args));
    }

    private void importJson(final Args args) {
        final long start = currentTimeMillis();
        JsonWhitelistService json = (JsonWhitelistService) storageFactory.loadStorage(JSON);
        locale.localize(IMPORT_LOADED)
                .set("loaded", json.getWhitelist().size())
                .set("storage", WHITELIST_FILE_NAME)
                .send(args.getSender());
        final OrmliteWhitelistService database = (OrmliteWhitelistService) whitelistService;
        final Set<Whitelistable> whitelist = json.getWhitelist();
        final Collection<TimesAutoWhitelistedNumber> autoWhitelist = json.getAutoWhitelist().values();
        final int whitelistImportedInTotal, autoWhitelistImportedInTotal;
        if (args.hasOption(REPLACE)) {
            whitelistImportedInTotal = database.addAllReplacing(whitelist).join();
            autoWhitelistImportedInTotal = database.addAllAutoReplacing(autoWhitelist).join();
        } else {
            whitelistImportedInTotal = database.addAllIfNotExists(whitelist).join();
            autoWhitelistImportedInTotal = database.addAllAutoIfNotExists(autoWhitelist).join();
        }
        final long end = currentTimeMillis();
        locale.localize(WHITELIST_IMPORT_RESULT)
                .set("imported", whitelistImportedInTotal).set("loaded", whitelist.size())
                .set("auto-imported", autoWhitelistImportedInTotal).set("auto-loaded", autoWhitelist.size())
                .set("ms-spent", end - start)
                .send(args.getSender());
    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.isEmpty())
            return emptyList();
        if (args.getLastArg().isPresent() && args.getLastArg().get().isOption())
            return completeOption(args.getLast());
        return emptyList();
    }
}
