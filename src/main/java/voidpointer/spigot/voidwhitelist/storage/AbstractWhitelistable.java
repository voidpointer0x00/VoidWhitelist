package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.util.Date;

public abstract class AbstractWhitelistable implements Whitelistable {
    @Override public boolean isAllowedToJoin() {
        if (!isExpirable())
            return true;
        return getExpiresAt().after(new Date());
    }

    @Override public boolean isExpirable() {
        return Whitelistable.NEVER_EXPIRES != getExpiresAt();
    }

    public abstract Date getExpiresAt();

    public abstract void setExpiresAt(final Date date);
}
