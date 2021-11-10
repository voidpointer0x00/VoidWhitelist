package voidpointer.spigot.voidwhitelist.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class WhitelistDisabledEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistDisabledEvent() {
        super(true);
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
