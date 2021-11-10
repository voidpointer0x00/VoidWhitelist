package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;

@Getter
public final class WhitelistAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final String nickname;
    private final Date expiresAt;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistAddedEvent(final String nickname, final Date expiresAt) {
        super(true);
        this.nickname = nickname;
        this.expiresAt = expiresAt;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
