package voidpointer.spigot.voidwhitelist.papi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import voidpointer.spigot.framework.localemodule.Message;

@RequiredArgsConstructor
public enum PapiMessage implements Message {
    EXPIRED("expired"), NEVER("never"), NOT_WHITELISTED("not whitelisted");
    private static final String PREFIX = "papi-";
    @Getter private final String defaultMessage;

    @Override public String getPath() {
        return PREFIX + toString().toLowerCase().replace('_', '-');
    }
}
