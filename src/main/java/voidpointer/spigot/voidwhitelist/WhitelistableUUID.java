package voidpointer.spigot.voidwhitelist;

import java.util.UUID;

public interface WhitelistableUUID extends Whitelistable {
    UUID getUniqueId();
}
