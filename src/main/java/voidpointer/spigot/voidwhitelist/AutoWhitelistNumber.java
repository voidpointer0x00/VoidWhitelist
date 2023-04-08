package voidpointer.spigot.voidwhitelist;

public interface AutoWhitelistNumber {
    AutoWhitelistNumber ZERO = () -> 0;

    static AutoWhitelistNumber of(final int timesAutoWhitelisted) {
        return () -> timesAutoWhitelisted;
    }

    int get();

    default boolean isExceeded(final int limit) {
        return (limit >= 0) && (get() >= limit);
    }
}
