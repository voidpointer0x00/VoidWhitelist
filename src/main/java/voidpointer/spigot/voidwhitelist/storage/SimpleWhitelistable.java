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
package voidpointer.spigot.voidwhitelist.storage;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public final class SimpleWhitelistable extends AbstractWhitelistable implements Comparable<SimpleWhitelistable> {
    @NonNull
    @EqualsAndHashCode.Include
    private UUID uniqueId;
    private Date expiresAt;
    private Date createdAt;

    public SimpleWhitelistable(final UUID uniqueId, final Date expiresAt) {
        this(uniqueId, expiresAt, new Date());
    }

    public SimpleWhitelistable(final UUID uniqueId, final Date expiresAt, final Date createdAt) {
        this.uniqueId = uniqueId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getUniqueId().equals(uniqueId);
    }

    @Override public String toString() {
        return uniqueId.toString();
    }

    @Override public int compareTo(@NonNull final SimpleWhitelistable o) {
        if (uniqueId.equals(o.uniqueId))
            return 0;
        if (createdAt.compareTo(o.createdAt) <= 0)
            return -1;
        return 1;
    }
}
