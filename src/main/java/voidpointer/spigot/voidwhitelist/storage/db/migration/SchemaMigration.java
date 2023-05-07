package voidpointer.spigot.voidwhitelist.storage.db.migration;

import voidpointer.spigot.voidwhitelist.storage.db.OrmliteDatabase;

import java.sql.SQLException;

interface SchemaMigration {
    String name();

    void perform(final OrmliteDatabase database) throws SQLException;
}
