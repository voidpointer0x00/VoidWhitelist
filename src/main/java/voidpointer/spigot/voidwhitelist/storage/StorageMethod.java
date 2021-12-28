package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.storage.serial.SerialNameWhitelistService;

public enum StorageMethod {
    /**
     * Based on standard Java serialization.
     *
     * @see SerialNameWhitelistService
     */
    SERIAL,
    /**
     * Based on JSON serialization libraries. Considered to be more
     *  human-friendly than serialization and fast enough.
     *
     * @see voidpointer.spigot.voidwhitelist.storage.json.JsonWhitelistService
     */
    JSON,
    /* TODO: SQL database. */
    ;
}
