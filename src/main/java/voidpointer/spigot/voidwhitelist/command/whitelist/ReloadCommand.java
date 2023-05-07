package voidpointer.spigot.voidwhitelist.command.whitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.di.Injector;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.framework.localemodule.config.TranslatedLocaleFile;
import voidpointer.spigot.voidwhitelist.VoidWhitelistPlugin;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistReloadEvent;
import voidpointer.spigot.voidwhitelist.papi.PapiLocale;
import voidpointer.spigot.voidwhitelist.storage.AutoWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.StorageFactory;
import voidpointer.spigot.voidwhitelist.storage.StorageMethod;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetchers;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

public final class ReloadCommand extends Command {
    public static final String NAME = "reload";

    @AutowiredLocale private static Locale locale;
    @Autowired private static LocaleLog guiLocale;
    @Autowired private static PapiLocale papiLocale;
    @Autowired(mapId="plugin")
    private static VoidWhitelistPlugin plugin;
    @Autowired private static StorageFactory storageFactory;
    @Autowired private static WhitelistConfig config;
    @Autowired private static EventManager eventManager;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;

    public ReloadCommand() {
        super(NAME);
    }

    @Override public void execute(final Args args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            //noinspection SynchronizeOnNonFinalField
            synchronized (config) {
                reloadConfig(args);
                reloadGeneralLocale(args);
                reloadGuiLocale(args);
                papiLocale.reload(); // TODO notify about reload? meh
                reloadStorage(args);
            }
        });
    }

    private void reloadConfig(final Args args) {
        config.reload();
        config.runMigrations();
        UUIDFetchers.updateMode(config.isUUIDModeOnline());
        locale.localize(CONFIG_RELOADED).send(args.getSender());
    }

    private void reloadGuiLocale(final Args args) {
        if (!(guiLocale instanceof TranslatedLocaleFile)) {
            locale.localize(GUI_LOCALE_DOESNT_SUPPORT_RELOAD).send(args.getSender());
            return;
        }
        reloadLocale(guiLocale);
        locale.localize(GUI_LOCALE_RELOADED).send(args.getSender());
    }

    private void reloadGeneralLocale(final Args args) {
        if (!(locale instanceof TranslatedLocaleFile)) {
            locale.localize(LOCALE_DOESNT_SUPPORT_RELOAD).send(args.getSender());
            return;
        }
        reloadLocale(locale);
        locale.localize(LOCALE_RELOADED).send(args.getSender());
    }

    private void reloadLocale(final Locale locale) {
        TranslatedLocaleFile translatedLocale = (TranslatedLocaleFile) locale;
        if (translatedLocale.getLanguage().equals(config.getLanguage()))
            translatedLocale.reload();
        else
            translatedLocale.changeLanguage(config.getLanguage());
    }

    private void reloadStorage(final Args args) {
        final StorageMethod oldMethod = whitelistService.getStorageMethod();
        if (oldMethod.equals(config.getStorageMethod()))
            return;

        AutoWhitelistService reloadedStorage = storageFactory.loadStorage(config.getStorageMethod());
        whitelistService.shutdown();
        plugin.changeWhitelistService(reloadedStorage);
        Injector.inject(plugin);

        locale.localize(STORAGE_METHOD_CHANGED)
                .set("old", oldMethod)
                .set("new", config.getStorageMethod())
                .send(args.getSender());

        eventManager.callAsyncEvent(new WhitelistReloadEvent(args.getSender()));
    }
}
