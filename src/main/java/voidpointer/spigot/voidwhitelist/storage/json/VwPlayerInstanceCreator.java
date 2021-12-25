package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.InstanceCreator;
import voidpointer.spigot.voidwhitelist.WhitelistableName;

import java.lang.reflect.Type;

final class VwPlayerInstanceCreator implements InstanceCreator<WhitelistableName> {
    @Override public WhitelistableName createInstance(final Type type) {
        return new JsonWhitelistableNamePojo();
    }
}
