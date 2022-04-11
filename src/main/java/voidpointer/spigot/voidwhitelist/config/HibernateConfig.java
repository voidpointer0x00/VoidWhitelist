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

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;

import java.io.File;
import java.util.Properties;

public final class HibernateConfig {
    public static final String FILENAME = "database.yml";
    private static final String DEFAULT_DBMS = "h2";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DBMS_PATH = "dbms";
    private static final String HOST_PATH = "host";
    private static final String PORT_PATH = "port";
    private static final String USER_PATH = "user";
    private static final String PASSWORD_PATH = "password";

    @AutowiredLocale private static LocaleLog log;

    private final Plugin plugin;
    private final File configFile;
    private YamlConfiguration config;
    private Configuration hibernateConfig;
    private SessionFactory sessionFactory;

    public HibernateConfig(final Plugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), FILENAME);
        load();
    }

    public void addEntity(final Class<?> entity) {
        hibernateConfig.addAnnotatedClass(entity);
    }

    public SessionFactory getSessionFactory() {
        assert hibernateConfig != null : "Hibernate config should've been initialized when the config was loaded";
        if (sessionFactory != null)
            return sessionFactory;
        sessionFactory = hibernateConfig.buildSessionFactory();
        return sessionFactory;
    }

    private void buildSessionFactory() {
        try {
            buildSessionFactory0();
        } catch (final HibernateException hibernateException) {
            log.warn("Unable to create database connection", hibernateException);
        }
    }

    private void buildSessionFactory0() throws HibernateException {
        sessionFactory = hibernateConfig.buildSessionFactory();
    }

    private void load() {
        if (!configFile.exists())
            plugin.saveResource(FILENAME, true);
        config = YamlConfiguration.loadConfiguration(configFile);
        initializeHibernateConfig();
    }

    private void initializeHibernateConfig() {
        DbmsFactory dbmsFactory = new DbmsFactory(plugin);
        DbmsProperties dbmsProps = dbmsFactory.matchingOrDefault(getDbms());
        Properties hibernateProps = dbmsProps.of(this);
        hibernateConfig = new Configuration();
        hibernateConfig.addProperties(hibernateProps);
    }

    private String getDbms() {
        if (config.isSet(DBMS_PATH))
            return config.getString(DBMS_PATH);
        config.set(DBMS_PATH, DEFAULT_DBMS);
        return DEFAULT_DBMS;
    }
}
