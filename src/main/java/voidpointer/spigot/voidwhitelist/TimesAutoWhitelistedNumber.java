package voidpointer.spigot.voidwhitelist;

import java.util.UUID;

public interface TimesAutoWhitelistedNumber {
    static TimesAutoWhitelistedNumber zero(final UUID uniqueId) {
        return of(uniqueId, 0);
    }

    static TimesAutoWhitelistedNumber of(final UUID uniqueId, final int timesAutoWhitelisted) {
        return new TimesAutoWhitelistedNumber() {
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
