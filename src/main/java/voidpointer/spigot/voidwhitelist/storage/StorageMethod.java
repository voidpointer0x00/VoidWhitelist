package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService;
import voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService;

public enum StorageMethod {
    /**
     * Based on standard Java serialization.
     *
     * @see SerialWhitelistService
     */
    SERIAL,
    /**
     * Based on JSON serialization libraries. Considered to be more
     *  human-friendly than serialization and fast enough.
     *
     * @see JsonWhitelistService
     */
    JSON,
    /* TODO: SQL database. */
    ;
}
