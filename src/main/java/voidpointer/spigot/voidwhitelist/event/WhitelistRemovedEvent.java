package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import voidpointer.spigot.voidwhitelist.Whitelistable;

@Getter
public final class WhitelistRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final Whitelistable whitelistable;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistRemovedEvent(final Whitelistable whitelistable) {
        super(true);
        this.whitelistable = whitelistable;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
