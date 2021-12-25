package voidpointer.spigot.voidwhitelist.storage;

import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.WhitelistableName;

import java.util.Date;

public abstract class AbstractWhitelistableName implements WhitelistableName {
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

    public abstract String getName();

    @Override public final String toString() {
        return getName();
    }
}
