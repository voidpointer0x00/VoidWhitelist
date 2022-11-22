package voidpointer.spigot.voidwhitelist.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.time.Duration;
import java.util.Optional;

import static voidpointer.spigot.voidwhitelist.papi.PapiMessage.EXPIRED;
import static voidpointer.spigot.voidwhitelist.papi.PapiMessage.NEVER;
import static voidpointer.spigot.voidwhitelist.papi.PapiMessage.NOT_WHITELISTED;

public class TimeLeftExpansion extends PlaceholderExpansion {
    @AutowiredLocale private static LocaleLog locale;
    @Autowired(mapId="plugin")
    private static Plugin plugin;
    @Autowired private static WhitelistService whitelistService;

    @Override public @NotNull String getIdentifier() {
        return "vw";
    }

    @Override public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override public boolean persist() {
        return true;
    }

    @Override public boolean register() {
        final boolean isRegistered = super.register();
        if (isRegistered)
            locale.info("Registered PAPI extension");
        return isRegistered;
    }

    @Override public @Nullable String onRequest(final OfflinePlayer player, @NotNull final String params) {
        if (player == null || !params.equalsIgnoreCase("time-left"))
            return null;
        Optional<Whitelistable> optionalWhitelistable = whitelistService.find(player.getUniqueId()).join();
        if (optionalWhitelistable.isPresent())
            return getTimeLeftFor(optionalWhitelistable.get());
        return locale.localize(NOT_WHITELISTED).getRawMessage();
    }

    private String getTimeLeftFor(final Whitelistable whitelistable) {
        if (!whitelistable.isExpirable())
            return locale.localize(NEVER).getRawMessage();
        final long millisBeforeExpires = whitelistable.getExpiresAt().getTime() - System.currentTimeMillis();
        if (millisBeforeExpires <= 0)
            return locale.localize(EXPIRED).getRawMessage();
        return Duration.ofMillis(millisBeforeExpires).toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
