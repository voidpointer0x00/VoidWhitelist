package voidpointer.spigot.voidwhitelist;

public interface TimesAutoWhitelistedNumber {
    TimesAutoWhitelistedNumber ZERO = () -> 0;

    static TimesAutoWhitelistedNumber of(final int timesAutoWhitelisted) {
        return () -> timesAutoWhitelisted;
    }

    int get();

    default boolean isExceeded(final int limit) {
        return (limit >= 0) && (get() >= limit);
    }
}
