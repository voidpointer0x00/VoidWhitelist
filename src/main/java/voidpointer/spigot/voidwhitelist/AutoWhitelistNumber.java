package voidpointer.spigot.voidwhitelist;

public interface AutoWhitelistNumber {
    AutoWhitelistNumber ZERO = () -> 0;

    static AutoWhitelistNumber of(final int timesAutoWhitelisted) {
        return () -> timesAutoWhitelisted;
    }

    int getTimesAutoWhitelisted();

    default boolean isExceeded(final int limit) {
        return (limit >= 0) && (getTimesAutoWhitelisted() >= limit);
    }
}
