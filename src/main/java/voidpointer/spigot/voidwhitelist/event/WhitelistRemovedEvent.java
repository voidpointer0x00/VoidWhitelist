package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class WhitelistRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final String nickname;

    public static final HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistRemovedEvent(final String nickname) {
        super(true);
        this.nickname = nickname;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
