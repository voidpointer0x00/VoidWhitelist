package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import voidpointer.spigot.voidwhitelist.Whitelistable;

@Getter
public final class WhitelistRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Whitelistable whitelistable;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistRemovedEvent(final Whitelistable whitelistable) {
        this.whitelistable = whitelistable;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
