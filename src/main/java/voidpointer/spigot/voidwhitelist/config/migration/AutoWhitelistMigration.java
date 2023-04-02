package voidpointer.spigot.voidwhitelist.config.migration;

import org.bukkit.configuration.ConfigurationSection;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

final class AutoWhitelistMigration implements ConfigMigration {
    private static final List<String> AUTO_WL_COMMENTS = asList(
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
    private static final List<String> AUTO_WL_MAX_REPEATS_COMMENTS = asList(
            " Any value in range (-∞;0] will disable the auto whitelisting.",
            "Although, it should not be used that way, because if you want to disable this feature",
            "then simply disable it, using < 1 \"max-repeats\" will still make the plugin run multiple",
            "checks when a player logins, wastefully spending your important clock-cycles and their",
            "login time."
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
        config.set(WhitelistConfig.AUTO_WL_MAX_REPEATS_PATH, 1);
        config.set(WhitelistConfig.AUTO_WL_STRATEGY_PATH, "newcomers");

        /* unset previous shitty unimplemented configuration */
        config.set("auto-whitelist-new-players", null);
        config.set("auto-whitelist-time", null);

        if (supportsComments(config)) { // TODO test on 1.18.1, 1.18, 1.17
            config.setComments(WhitelistConfig.AUTO_WL_PATH, AUTO_WL_COMMENTS);
            config.setComments(WhitelistConfig.AUTO_WL_DURATION_PATH, AUTO_WL_DURATION_COMMENTS);
            config.setComments(WhitelistConfig.AUTO_WL_MAX_REPEATS_PATH, AUTO_WL_MAX_REPEATS_COMMENTS);
            config.setComments(WhitelistConfig.AUTO_WL_STRATEGY_PATH, AUTO_WL_STRATEGY_COMMENTS);
        }
    }

    private boolean supportsComments(final ConfigurationSection config) {
        /* SpigotMC only introduced comment manipulation methods in 1.18.2
         *  if we can trust the release & commit dates commit 3e2dd2bc120 on 20 dec 2021,
         *  Spigot version 1.18.1 released on 10 dec 2021, 1.18.2 on 6 mar 2022
         * https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/commits/3e2dd2bc120754ea4db193e878050d0eb31a6894
         */
        List<String> commentMethods = asList("getComments", "getInlineComments", "setComments", "setInlineComments");
        for (final Method method : config.getClass().getMethods()) {
            if (commentMethods.contains(method.getName()))
                return true;
        }
        return false;
    }
}
