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
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistImportEvent;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;

import java.util.List;
import java.util.Set;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static voidpointer.spigot.voidwhitelist.command.ImportOptions.REPLACE;
import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;
import static voidpointer.spigot.voidwhitelist.storage.StorageMethod.JSON;
import static voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService.WHITELIST_FILE_NAME;

public class ImportJsonCommand extends Command {
    public static final String NAME = "import-json";
    public static final String PERMISSION = "permission";

    @AutowiredLocale private static Locale locale;
    @Autowired private static EventManager eventManager;
    @Autowired private static WhitelistService whitelistService;
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
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> execute0(args));
    }

    private void execute0(final Args args) {
        final long start = currentTimeMillis();
        JsonWhitelistService json = (JsonWhitelistService) storageFactory.loadStorage(JSON);
        locale.localize(IMPORT_LOADED)
                .set("loaded", json.getWhitelist().size())
                .set("storage", WHITELIST_FILE_NAME)
                .send(args.getSender());
        OrmliteWhitelistService database = (OrmliteWhitelistService) ImportJsonCommand.whitelistService;
        Set<Whitelistable> imported;
        if (args.hasOption(REPLACE))
            imported = database.addAllReplacing(json.getWhitelist()).join();
        else
            imported = database.addAllIfNotExists(json.getWhitelist()).join();
        final long end = currentTimeMillis();
        locale.localize(IMPORT_RESULT)
                .set("imported", imported.size())
                .set("loaded", json.getWhitelist().size())
                .set("ms-spent", end - start)
                .send(args.getSender());
        eventManager.callAsyncEvent(new WhitelistImportEvent(imported));
    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.isEmpty())
            return emptyList();
        return completeOption(args.getLast());
    }
}
