package voidpointer.spigot.voidwhitelist.config.migration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public final class WhitelistConfigMigrationFactory {
    public static Collection<ConfigMigration> getAllMigrations() {
        /* migrations will only run once when the server is loading,
         *  so we don't need to save or cache migrations anywhere,
         *  just create, send, execute and forget about it. */
        final Queue<ConfigMigration> migrations = new LinkedList<>();
        migrations.add(new AutoWhitelistMigration());
        migrations.add(new RepeatsLimitMigration());
        return migrations;
    }
}
