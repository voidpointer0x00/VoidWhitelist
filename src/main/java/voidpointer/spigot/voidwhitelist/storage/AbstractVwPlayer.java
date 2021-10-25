package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.util.Date;

public abstract class AbstractVwPlayer implements VwPlayer {
    @Override public boolean isAllowedToJoin() {
        if (!isExpirable())
            return true;
        return getExpiresAt().after(new Date());
    }

    @Override public boolean isExpirable() {
        return VwPlayer.NEVER_EXPIRES != getExpiresAt();
    }

    public abstract Date getExpiresAt();

    public abstract void setExpiresAt(final Date date);
}
