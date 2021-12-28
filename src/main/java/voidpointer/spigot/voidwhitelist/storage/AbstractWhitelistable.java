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

    public abstract Date getExpiresAt();

    public abstract void setExpiresAt(final Date date);
}
