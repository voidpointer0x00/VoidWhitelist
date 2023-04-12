package voidpointer.spigot.voidwhitelist.storage.db.migration;

import lombok.RequiredArgsConstructor;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.storage.db.OrmliteDatabase;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class OrmliteMigrationService {
    @AutowiredLocale private static LocaleLog log;
    private final OrmliteDatabase database;

    public CompletableFuture<Optional<Set<String>>> findAllFinished() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(findAllFinished0());
            } catch (final SQLException sqlException) {
                log.warn("Unable to get finished migrations list: {0}", sqlException.getMessage());
                return Optional.empty();
            }
        });
    }

    void setFinished(final String migrationName) throws SQLException {
        database.getMigrationDao().createOrUpdate(new MigrationModel(migrationName, true));
    }

    private Set<String> findAllFinished0() throws SQLException {
        return database.getMigrationDao().queryForMatching(new MigrationModel(null, true)).stream()
                .map(MigrationModel::getName).collect(Collectors.toSet());
    }
}
