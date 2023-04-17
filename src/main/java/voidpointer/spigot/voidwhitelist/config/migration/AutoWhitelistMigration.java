package voidpointer.spigot.voidwhitelist.config.migration;

import org.bukkit.configuration.ConfigurationSection;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import java.util.List;

import static java.util.Arrays.asList;

final class AutoWhitelistMigration extends AbstractMigration {
    private static final List<String> AUTO_WL_COMMENTS = asList(
            "",
            " When both whitelist and auto-whitelist are enabled, the plugin will automatically",
            "add a joining player of selected group to the whitelist for the specified duration.",
            "",
            " A single player can only get automatically whitelisted \"max-repeats\" times.",
            "So if it's set to 2, after the first period they will be able to get extra time.",
            "* It won't work the second time IF they do not meet current strategy requirements.",
            " Strategies select a group of players who will be automatically whitelisted",
            "upon joining the server."
    );
    private static final List<String> AUTO_WL_DURATION_COMMENTS = asList(
            " Any value in range (-∞;0] will be treated as permanent whitelisting,",
            "WHICH SHOULD NOT BE USED (because then what's the point in using a whitelist?)",
            "",
            " Any value that can not be parsed will result in disabling the automatic",
            "whitelisting feature until the configuration is fixed and reloaded."
    );
    private static final List<String> AUTO_WL_STRATEGY_COMMENTS = asList(
            "all — simply every joining player if the number that they've been \"freely\" whitelisted",
            "      have yet to exceed the limiting \"max-repeats\" property.",
            "",
            "newcomers — only those players who have never player on this server before.",
            "This strategy makes use of Bukkit's #hasPlayerBefore() method that checks",
            "whether the server has any data on the given player. This will always",
            "be true if you disable the property «players.disable-saving» in spigot.yml"
    );

    @Override public boolean isUpToDate(final ConfigurationSection config) {
        /* basically just check whether the configuration has auto-whitelist
         *  section. Do not check whether it's valid or not - in that case we
         *  just blame the user, not going to fool-proof this stuff, it's easier
         *  to regenerate config.yml for them, than handling invalid auto-whitelist
         *  section just so NOBODY ever actually uses it. */
        return config.isSet(WhitelistConfig.AUTO_WL_PATH);
    }

    @Override public void run(final ConfigurationSection config) {
        config.set(WhitelistConfig.AUTO_WL_ENABLED_PATH, false);
        config.set(WhitelistConfig.AUTO_WL_DURATION_PATH, "7d");
        config.set(WhitelistConfig.AUTO_WL_STRATEGY_PATH, "all");

        /* unset previous shitty unimplemented configuration */
        config.set("auto-whitelist-new-players", null);
        config.set("auto-whitelist-time", null);

        if (supportsComments(config)) {
            config.setComments(WhitelistConfig.AUTO_WL_PATH, AUTO_WL_COMMENTS);
            config.setComments(WhitelistConfig.AUTO_WL_DURATION_PATH, AUTO_WL_DURATION_COMMENTS);
            config.setComments(WhitelistConfig.AUTO_WL_STRATEGY_PATH, AUTO_WL_STRATEGY_COMMENTS);
        }
    }
}
