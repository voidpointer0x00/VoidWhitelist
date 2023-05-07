package voidpointer.spigot.voidwhitelist.storage.db.migration;

import com.j256.ormlite.misc.TransactionManager;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteDatabase;

import java.sql.SQLException;
import java.util.Queue;

public final class SchemaMigrator {
    @AutowiredLocale private static LocaleLog log;
    private final OrmliteDatabase database;
    private final OrmliteMigrationService migrationService;

    public SchemaMigrator(final OrmliteDatabase database) {
        this.database = database;
        this.migrationService = new OrmliteMigrationService(database);
    }

    public void runUnfinishedMigrations() {
        migrationService.findAllFinished().join().ifPresent(finishedMigrations -> {
            final Queue<SchemaMigration> migrations = SchemaMigrationRepository.migrations();
            while (!migrations.isEmpty()) {
                final SchemaMigration migration = migrations.poll();
                if (finishedMigrations.contains(migration.name()))
                    continue;
                if (!performMigration(migration))
                    break;
            }
        });
    }

    private boolean performMigration(final SchemaMigration migration) {
        try {
            log.info("Performing {0} migration", migration.name());
            final long start = System.currentTimeMillis();
            TransactionManager.callInTransaction(database.getConnectionSource(), () -> {
                migration.perform(database);
                migrationService.setFinished(migration.name());
                return null;
            });
            log.info("{0} finished in {1} ms.", migration.name(), System.currentTimeMillis() - start);
            return true;
        } catch (final SQLException sqlException) {
            log.severe("Unable to perform migration {0}: {1}", migration.name(), sqlException.getMessage());
            return false;
        }
    }
}
