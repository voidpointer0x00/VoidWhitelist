package voidpointer.spigot.voidwhitelist.papi;

import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.config.LocaleFile;

import java.io.File;

public final class PapiLocale extends LocaleFile {
    public static final String FILENAME = "papi.yml";

    public PapiLocale(final Plugin plugin) {
        super.setPlugin(plugin);
        super.setMessagesFile(new File(plugin.getDataFolder(), FILENAME));
        saveDefaultMessagesFileIfNotExists();
        loadFileConfiguration();
        super.addDefaults(PapiMessage.values());
    }
}
