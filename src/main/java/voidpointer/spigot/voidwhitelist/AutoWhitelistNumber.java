package voidpointer.spigot.voidwhitelist;

public interface AutoWhitelistNumber {
    int getTimesAutoWhitelisted();

    boolean isExceeded(final int limit);
}
