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
package voidpointer.spigot.voidwhitelist.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.util.Properties;

@RequiredArgsConstructor
final class DbmsFactory {
    @AutowiredLocale private static LocaleLog log;

    private final Plugin plugin;

    public DbmsProperties matchingOrDefault(final String dbmsName) {
        switch (dbmsName.toLowerCase()) {
            case "h2":
                return this::h2;
            default:
                log.info("Unknown DBMS named {0}, using default H2", dbmsName);
                return this::h2;
        }
    }

    private Properties h2(final HibernateConfig hibernateConfig) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
