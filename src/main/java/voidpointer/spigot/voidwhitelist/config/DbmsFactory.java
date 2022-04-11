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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static java.lang.Boolean.FALSE;

@RequiredArgsConstructor
final class DbmsFactory {
    private static final String DRIVER_KEY = "hibernate.connection.driver_class";
    private static final String URL_KEY = "hibernate.connection.url";

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

    private void applyDefaults(final Properties props) {
        props.setProperty("hibernate.show_sql", FALSE.toString());
        props.setProperty("hibernate.hbm2ddl.auto", "update");
    }

    private Properties h2(final HibernateConfig hibernateConfig) {
        Properties props = new Properties();
        File h2File = new File(plugin.getDataFolder(), "h2");
        if (!h2File.exists())
            createFile(h2File);
        assert h2File.exists() : "Cannot continue without database file";
        props.setProperty(DRIVER_KEY, "org.h2.Driver");
        props.setProperty(URL_KEY, "jdbc:h2:" + h2File.getAbsolutePath());
        applyDefaults(props);
        return props;
    }

    private void createFile(final File file) {
        try {
            if (!file.createNewFile())
                log.warn("Unable to create " + file.getAbsolutePath());
        } catch (final IOException ioException) {
            log.warn("Unable to create " + file.getAbsolutePath(), ioException);
        }
    }
}
