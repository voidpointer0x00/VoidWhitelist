/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface Whitelistable {
    Date NEVER_EXPIRES = null;

    /**
     * Searches for an online player who's associated with this entity.
     *
     * @see Bukkit#getOnlinePlayers()
     * @see Bukkit#getPlayer(UUID)
     * @see Bukkit#getPlayer(String)
     * @return online player associated with this whitelistable entity if one exists.
     */
    Optional<Player> findAssociatedOnlinePlayer();

    UUID getUniqueId();

    String getName();

    void setName(final String name);

    /**
     * Checks whether this entity is associated with provided player or not.
     *
     * @return {@code true} if the player is associated with this whitelistable
     * entity, {@code false} otherwise.
     */
    boolean isAssociatedWith(final Player player);

    /**
     * @return {@code true} if the given {@link Date} can expire.
     * @see #NEVER_EXPIRES
     */
    static boolean isDateExpirable(final Date date) {
        return NEVER_EXPIRES != date;
    }

    /**
     * Checks whether associated player is allowed to join the server
     *  or not.
     *
     * @see #findAssociatedOnlinePlayer()
     * @return {@code true} if the player is allowed to join the server
     * and {@code false} otherwise.
     */
    boolean isAllowedToJoin();

    /**
     * Checks if this entity has date limit in whitelist (whether
     *  it is expirable or not).
     *
     * @return {@code true} if the entity has an "expires at"
     * date and {@code false} otherwise. If it is expirable
     * {@link #getExpiresAt()} method will return the Date
     * object, otherwise {@code null} will be returned.
     */
    boolean isExpirable();

    /**
     * Gets the "expires at" date. After that date the associated player
     * is no longer allowed to join the server.
     * <p>
     * So if the player is allowed to join, but there's no "expires at" time
     * the player is whitelisted forever, otherwise the player have never been
     * whitelisted. <b>The {@link #isExpirable()} method should be used to
     * check the state described above.</b>
     *
     * @return {@code null} if player is either whitelisted forever or have never
     * been whitelisted<i>(depends on the implementation and internal state)</i>.
     */
    Date getExpiresAt();
}
