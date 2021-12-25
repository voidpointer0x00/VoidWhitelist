package voidpointer.spigot.voidwhitelist;

import org.bukkit.entity.Player;

import java.util.Optional;

public interface WhitelistableName extends Whitelistable {
    /** Searches for an online player associated with this entity. */
    Optional<Player> findAssociatedOnlinePlayer();
}
