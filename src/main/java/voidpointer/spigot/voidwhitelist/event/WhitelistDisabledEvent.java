package voidpointer.spigot.voidwhitelist.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class WhitelistDisabledEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static final HandlerList getHandlerList() {
        return handlers;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
