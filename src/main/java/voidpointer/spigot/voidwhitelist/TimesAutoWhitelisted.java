package voidpointer.spigot.voidwhitelist;

import java.util.UUID;

public interface TimesAutoWhitelisted {
    static TimesAutoWhitelisted zero(final UUID uniqueId) {
        return of(uniqueId, 0);
    }

    static TimesAutoWhitelisted of(final UUID uniqueId, final int timesAutoWhitelisted) {
        return new TimesAutoWhitelisted() {
            @Override public UUID getUniqueId() {
                return uniqueId;
            }
            @Override public int get() {
                return timesAutoWhitelisted;
            }
        };
    }

    UUID getUniqueId();

    int get();

    default boolean isExceeded(final int limit) {
        return (limit >= 0) && (get() >= limit);
    }
}
