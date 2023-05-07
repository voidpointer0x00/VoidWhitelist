package voidpointer.spigot.voidwhitelist.storage.db.migration;

import java.util.LinkedList;
import java.util.Queue;

final class SchemaMigrationRepository {
    public static Queue<SchemaMigration> migrations() {
        final Queue<SchemaMigration> migrations = new LinkedList<>();
        migrations.add(new AutoWhitelistSchemaMigration());
        return migrations;
    }
}
