package voidpointer.spigot.voidwhitelist.config.migration;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

abstract class AbstractMigration implements ConfigMigration {
    protected boolean supportsComments(final ConfigurationSection config) {
        /*  Because this method will always return the same result,  it could be considered to
         * store the result in a static field, although I believe it is better to spend
         * a few more nanoseconds than to store an extra static variable. */

        /*  SpigotMC only introduced comment manipulation methods in 1.18.2
         * if we can trust the release & commit dates commit 3e2dd2bc120 on 20 dec 2021,
         * Spigot version 1.18.1 released on 10 dec 2021, 1.18.2 on 6 mar 2022
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
