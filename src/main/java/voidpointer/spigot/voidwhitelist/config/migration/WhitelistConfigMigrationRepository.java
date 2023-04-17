package voidpointer.spigot.voidwhitelist.config.migration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public final class WhitelistConfigMigrationRepository {
    public static Collection<ConfigMigration> getAllMigrations() {
        /*  Migrations will run once when the server is loading and
         * rarely if someone reloads the plugin, so we don't need
         * to save or cache migrations anywhere, just create, send,
         * execute and forget about it. */
        final Queue<ConfigMigration> migrations = new LinkedList<>();
        migrations.add(new AutoWhitelistMigration());
        migrations.add(new RepeatsLimitMigration());
        return migrations;
    }
}
