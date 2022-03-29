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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Date;
import java.util.Optional;

public abstract class AbstractWhitelistable implements Whitelistable {
    @Override public boolean isAllowedToJoin() {
        if (!isExpirable())
            return true;
        return getExpiresAt().after(new Date());
    }

    @Override public Optional<Player> findAssociatedOnlinePlayer() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(this::isAssociatedWith)
                .map(player -> (Player) player)
                .findFirst();
    }

    @Override public boolean isExpirable() {
        return Whitelistable.NEVER_EXPIRES != getExpiresAt();
    }

    @Override public boolean equals(final Object o) {
        if (null == o)
            return false;
        if (this == o)
            return true;
        if (!(o instanceof Whitelistable))
            return false;
        return getUniqueId().equals(((Whitelistable) o).getUniqueId());
    }

    @Override public int hashCode() {
        return getUniqueId().hashCode();
    }

    public abstract Date getExpiresAt();

    public abstract void setExpiresAt(final Date date);
}
