package voidpointer.spigot.voidwhitelist.storage.db.migration;

import voidpointer.spigot.voidwhitelist.storage.db.OrmliteDatabase;

import java.sql.SQLException;

final class AutoWhitelistSchemaMigration implements SchemaMigration {
    public static final String NAME = "auto_whitelist/whitelistable_rename";
    private static final String RENAME_COLUMNS = "ALTER TABLE whitelist RENAME COLUMN uniqueId TO unique_id" +
            ", RENAME COLUMN expiresAt TO expires_at";

    @Override public String name() {
        return NAME;
    }

    @Override public void perform(final OrmliteDatabase database) throws SQLException {
        database.getWhitelistDao().executeRaw(RENAME_COLUMNS);
    }
}
