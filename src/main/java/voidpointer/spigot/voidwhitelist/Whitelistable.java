package voidpointer.spigot.voidwhitelist;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Optional;

public interface Whitelistable {
    Date NEVER_EXPIRES = null;

    /** Returns online player associated with this whitelistable entity if one exists. */
    Optional<Player> findAssociatedOnlinePlayer();

    /** Returns {@code true} if the entity has an "expires at" date
     * and {@code false} otherwise. */
    static boolean isDateExpirable(final Date date) {
        return NEVER_EXPIRES != date;
    }

    /**
     * Returns {@code true} if the player is allowed to join the server
     * and {@code false} otherwise. Depends on the whitelist status
     * discussed in class overview.
     */
    boolean isAllowedToJoin();

    /**
     * Returns {@code true} if the entity has an "expires at"
     * date and {@code false} otherwise. If it is expirable
     * {@link #getExpiresAt()} method will return the Date
     * object, otherwise {@code null} will be returned.
     */
    boolean isExpirable();

    /**
     * Get the "expires at" date. After that date the associated player
     * is no longer allowed to join the server.
     * <p>
     * Returns {@code null} if player is either whitelisted forever or have never
     * been whitelisted<i>(depends on the implementation and internal state)</i>.
     * <p>
     * So if the player is allowed to join, but there's no "expires at" time
     * the player is whitelisted forever, otherwise the player have never been
     * whitelisted. <b>The {@link #isExpirable()} method should be used to
     * check the state described above.</b>
     */
    Date getExpiresAt();
}
