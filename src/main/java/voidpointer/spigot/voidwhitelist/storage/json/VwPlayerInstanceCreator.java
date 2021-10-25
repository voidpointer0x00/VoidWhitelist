package voidpointer.spigot.voidwhitelist.storage.json;

import com.google.gson.InstanceCreator;
import voidpointer.spigot.voidwhitelist.VwPlayer;

import java.lang.reflect.Type;

final class VwPlayerInstanceCreator implements InstanceCreator<VwPlayer> {
    @Override public VwPlayer createInstance(final Type type) {
        return new JsonVwPlayerPojo();
    }
}
