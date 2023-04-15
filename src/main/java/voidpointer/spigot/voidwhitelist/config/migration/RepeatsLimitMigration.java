package voidpointer.spigot.voidwhitelist.config.migration;

import org.bukkit.configuration.ConfigurationSection;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import java.util.List;

import static java.util.Arrays.asList;

final class RepeatsLimitMigration extends AbstractMigration {
    private static final String AUTO_WL_MAX_REPEATS_PATH = "auto-whitelist.max-repeats";
    private static final List<String> AUTO_WL_LIMIT_COMMENTS = asList(
            " This property controls the limit for the number of automatic whitelisting.",
            "",
            " A value of zero will disable the auto whitelisting. Any negative number will",
            "disable this property and players will be able to infinitely join the server",
            "and get whitelisted every time."
    );

    @Override public boolean isUpToDate(final ConfigurationSection config) {
        return config.isSet("limit");
    }

    @Override public void run(final ConfigurationSection config) {
        /*  During the development process of the auto-whitelist feature, "max-repeats"
         * property was introduced and later, after a few pre-release builds were published,
         * was renamed to "limit".
         *  So here we remove the max-repeats property if it's still there and create
         * the new one. */
        config.set(AUTO_WL_MAX_REPEATS_PATH, null);

        config.set(WhitelistConfig.AUTO_WL_LIMIT_PATH, 1);

        if (supportsComments(config))
            config.setComments(WhitelistConfig.AUTO_WL_LIMIT_PATH, AUTO_WL_LIMIT_COMMENTS);
    }
}
