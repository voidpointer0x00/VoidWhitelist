package voidpointer.spigot.voidwhitelist.storage;

public class NotWhitelistedException extends RuntimeException {
    public NotWhitelistedException(final String message) {
        super(message);
    }
}
