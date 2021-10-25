package voidpointer.spigot.voidwhitelist;

import java.util.Date;

/**
 * <p>VwPlayer (VoidWhitelistPlayer) is the main entity of the plugin.</p>
 *
 * <p>It is stored using one of {@link voidpointer.spigot.voidwhitelist.storage.StorageMethod}
 * and operated everywhere in the code. It represents whitelist status of
 * a specific player (nickname).</p>
 *
 * <p>The whitelist status is one of the following:</p>
 * <ul>
 *     <li>Whitelisted forever;</li>
 *     <li>Whitelisted until the <o>time</o> expires;</li>
 *     <li>Expired;</li>
 *     <li>Not whitelisted.</li>
 * </ul>
 *
 * <p>A "<b>whitelisted forever</b>" player is stored until they get removed manually.
 * The player is allowed to join the server.</p>
 *
 * <p>A "<b>whitelisted until the <o>time</o> expires</b>" player has a
 * {@link java.util.Date} at which the player's time will be
 * expired and after that day the player will no longer be allowed
 * to join the server. After the time is expired the entity <b>should</b>
 * be removed from the storage.</p>
 *
 * <p>A player <b>who's time is expired</b> is in the line for deletion from
 * the storage. It is not necessary to remove them immediately, but as
 * soon as they attempt to log in they should free the resources of storage.</p>
 *
 * <p><i>Though, it is likely that it will be revised and changed, so
 * that, for example, web interfaces could show that the player once
 * was whitelisted, but the time expired. There's a difference between
 * showing what needs to be renewed and that the player has never been
 * whitelisted.</i></p>
 *
 * <p><b>A not whitelisted player</b>. The player is not allowed to
 * join the server.</p>
 */
public interface VwPlayer {
    public static final Date NEVER_EXPIRES = null;

    /** Get the name of a Minecraft player associated with the entity. */
    String getName();

    /**
     * Returns true if the player is allowed to join the server
     * and false otherwise. Depends on the whitelist status discussed in
     * class overview.
     */
    boolean isAllowedToJoin();

    /**
     * Returns true if the entity has an "expires at" date and
     * false otherwise. If it is expirable {@link #getExpiresAt()} method
     * will return the Date object, otherwise null will be returned.
     */
    boolean isExpirable();

    /**
     * Get the "expires at" date. After that date the associated player
     * is no longer allowed to join the server.
     * <p>
     * Returns null if player is either whitelisted forever or have never
     * been whitelisted<i>(depends on the implementation and internal state)</i>.
     * So if the player is allowed to join, but there's no "expires at" time
     * the player is whitelisted forever, otherwise the player have never been
     * whitelisted. <b>The {@link #isExpirable()} method should be used to
     * check the state described above.</b>
     */
    Date getExpiresAt();
}
