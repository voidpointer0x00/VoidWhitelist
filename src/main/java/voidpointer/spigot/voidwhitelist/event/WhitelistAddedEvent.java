package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public final class WhitelistAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final String nickname;
    private final Date expiresAt;

    public static final HandlerList getHandlerList() {
        return handlers;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
