package voidpointer.spigot.voidwhitelist.config.migration;

import java.util.Collection;
import java.util.Collections;

public final class WhitelistConfigMigrationFactory {
    public static Collection<ConfigMigration> getAllMigrations() {
        /* migrations will only run once when the server is loading,
         *  so we don't need to save or cache migrations anywhere,
         *  just create, send & execute it and forget about it. */
        return Collections.singletonList(new AutoWhitelistMigration());
    }
}
