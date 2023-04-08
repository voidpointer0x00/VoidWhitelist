package voidpointer.spigot.voidwhitelist;

public interface AutoWhitelistNumber {
    AutoWhitelistNumber ZERO = () -> 0;

    int getTimesAutoWhitelisted();

    default boolean isExceeded(final int limit) {
        return (limit >= 0) && (getTimesAutoWhitelisted() >= limit);
    }
}
