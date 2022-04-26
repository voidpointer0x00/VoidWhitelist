package voidpointer.spigot.voidwhitelist.command;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.di.Injector;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFile;
import voidpointer.spigot.voidwhitelist.VoidWhitelistPlugin;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.net.DefaultUUIDFetcher;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class ReloadCommand extends Command {
    public static final String NAME = "reload";
    public static final String PERMISSION = "whitelist.reload";

    @AutowiredLocale private static Locale locale;
    @Autowired(mapId="plugin")
    private static VoidWhitelistPlugin plugin;
    @Autowired private static StorageFactory storageFactory;
    @Autowired private static WhitelistConfig config;
    @Autowired private static EventManager eventManager;
    @Autowired private static WhitelistService whitelistService;

    public ReloadCommand() {
        super(NAME);
        setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            //noinspection SynchronizeOnNonFinalField
            synchronized (config) {
                reloadConfig(args);
                reloadLocale(args);
                reloadStorage(args);
            }
        });
    }

    private void reloadConfig(final Args args) {
        config.reload();
        DefaultUUIDFetcher.updateMode(config.isUUIDModeOnline());
        locale.localize(CONFIG_RELOADED).send(args.getSender());
    }

    private void reloadLocale(final Args args) {
        if (!(locale instanceof TranslatedLocaleFile)) {
            locale.localize(LOCALE_DOESNT_SUPPORT_RELOAD).send(args.getSender());
            return;
        }
        TranslatedLocaleFile translatedLocale = (TranslatedLocaleFile) locale;
        if (translatedLocale.getLanguage().equals(config.getLanguage()))
            translatedLocale.reload();
        else
            translatedLocale.changeLanguage(config.getLanguage());
        locale.localize(LOCALE_RELOADED).send(args.getSender());
    }

    private void reloadStorage(final Args args) {
        final StorageMethod oldMethod = whitelistService.getStorageMethod();
        if (oldMethod.equals(config.getStorageMethod()))
            return;

        WhitelistService reloadedStorage = storageFactory.loadStorage(config.getStorageMethod());
        whitelistService.shutdown();
        plugin.changeWhitelistService(reloadedStorage);
        Injector.inject(plugin);

        locale.localize(STORAGE_METHOD_CHANGED)
                .set("old", oldMethod)
                .set("new", config.getStorageMethod())
                .send(args.getSender());

        if (config.isWhitelistEnabled())
            eventManager.callAsyncEvent(new WhitelistEnabledEvent());
    }
}
