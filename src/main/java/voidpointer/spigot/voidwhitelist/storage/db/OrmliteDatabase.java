package voidpointer.spigot.voidwhitelist.storage.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.OrmliteConfig;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.storage.db.migration.MigrationModel;
import voidpointer.spigot.voidwhitelist.storage.db.migration.SchemaMigrator;
import voidpointer.spigot.voidwhitelist.task.DatabaseSyncTask;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.UUID;

import static com.j256.ormlite.dao.DaoManager.createDao;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ConnectionResult.FAIL;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ConnectionResult.SUCCESS;

public final class OrmliteDatabase {
    @AutowiredLocale private static LocaleLog log;

    private final Plugin plugin;
    private final OrmliteConfig ormliteConfig;
    private final SchemaMigrator schemaMigrator;
    private DatabaseSyncTask syncTask;

    @Getter private ConnectionSource connectionSource;
    @Getter private Dao<WhitelistableModel, UUID> whitelistDao;
    @Getter private Dao<TimesAutoWhitelistedNumberModel, UUID> autoWhitelistDao;
    @Getter private Dao<MigrationModel, String> migrationDao;

    public OrmliteDatabase(final Plugin plugin) {
        this.plugin = plugin;
        ormliteConfig = new OrmliteConfig(plugin);
        schemaMigrator = new SchemaMigrator(this);
        Logger.setGlobalLogLevel(Level.OFF); // disable ORMLite logging
    }

    public WhitelistService.ConnectionResult connect() {
        log.info("Establishing database connection...");

        if (!(createConnectionSource() && createDataAccessObjects())) {
            log.warn("Connection failed.");
            return FAIL;
        }
        schemaMigrator.runUnfinishedMigrations();
        scheduleSync();
        log.info("Connection established.");
        return SUCCESS;
    }

    public WhitelistService.ConnectionResult reconnect() {
        shutdown();
        return !ormliteConfig.reload() ? FAIL : connect();
    }

    public void shutdown() {
        if (syncTask != null) {
            syncTask.cancel();
            syncTask = null;
        }
        if (connectionSource != null) {
            connectionSource.closeQuietly();
            connectionSource = null;
        }
    }

    public boolean isNotConnected() {
        return (connectionSource == null) || (whitelistDao == null) || (autoWhitelistDao == null);
    }

    private boolean createConnectionSource() {
        connectionSource = new DbmsFactory(plugin.getDataFolder()).matchingOrDefault(ormliteConfig.getDbms())
                        .newConnectionSource(ormliteConfig);
        return connectionSource != null;
    }

    private boolean createDataAccessObjects() {
        try {
            createDataAccessObjects0();
            return (whitelistDao != null) && (autoWhitelistDao != null);
        } catch (final SQLException sqlException) {
            log.warn("Unable to create data access objects: {0}",
                    getRootConnectionExceptionOrElse(sqlException, sqlException).getMessage());
            whitelistDao = null;
            autoWhitelistDao = null;
            migrationDao = null;
            return false;
        }
    }

    private void createDataAccessObjects0() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, WhitelistableModel.class);
        TableUtils.createTableIfNotExists(connectionSource, TimesAutoWhitelistedNumberModel.class);
        TableUtils.createTableIfNotExists(connectionSource, MigrationModel.class);

        whitelistDao = createDao(connectionSource, WhitelistableModel.class);
        autoWhitelistDao = createDao(connectionSource, TimesAutoWhitelistedNumberModel.class);
        migrationDao = createDao(connectionSource, MigrationModel.class);
    }

    private void scheduleSync() {
        if (ormliteConfig.isSyncEnabled()) {
            syncTask = new DatabaseSyncTask();
            syncTask.runTaskTimerAsynchronously(plugin, 0, ormliteConfig.getSyncTimerInTicks());
        }
    }

    private Throwable getRootConnectionExceptionOrElse(final SQLException sqlException, final Throwable throwable) {
        Throwable rootCause = sqlException;
        while ((rootCause.getCause() != null) && (rootCause != rootCause.getCause()))
            rootCause = rootCause.getCause();
        return (rootCause instanceof ConnectException) ? rootCause : throwable;
    }
}
