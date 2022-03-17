package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.InstanceCreator;
import voidpointer.spigot.voidwhitelist.Whitelistable;

import java.lang.reflect.Type;

final class WhitelistableInstanceCreator implements InstanceCreator<Whitelistable> {
    @Override public Whitelistable createInstance(final Type type) {
        return new JsonWhitelistableNamePojo();
    }
}
