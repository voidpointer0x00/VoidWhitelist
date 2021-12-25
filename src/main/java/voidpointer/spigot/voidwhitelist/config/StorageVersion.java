package voidpointer.spigot.voidwhitelist.config;

public enum StorageVersion {
    UNDEFINED,
    V1, /* the conversion is not implemented (and likely won't be) */
    V2, /* TODO: implement conversion to V3 */
    V3, /* Introduce UUID and IP as alternative to nickname based WhitelistableName ID */
    ;

    public static final StorageVersion CURRENT = V3;
}
