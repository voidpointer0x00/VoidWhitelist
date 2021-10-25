package voidpointer.spigot.voidwhitelist.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class EventManager {
    @NonNull private final Plugin plugin;

    public void callAsyncEvent(final Event event) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getServer().getPluginManager().callEvent(event));
    }
}
