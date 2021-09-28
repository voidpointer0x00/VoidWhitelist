package voidpointer.spigot.voidwhitelist.storage;

public enum StorageMethod {
    /**
     * Based on standard Java serialization.
     *
     * @see voidpointer.spigot.voidwhitelist.storage.serial.SerialWhitelistService
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
