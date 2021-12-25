package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import voidpointer.spigot.voidwhitelist.WhitelistableName;

@Getter
public final class WhitelistAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final WhitelistableName whitelistableName;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistAddedEvent(final WhitelistableName whitelistableName) {
        super(true);
        this.whitelistableName = whitelistableName;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
