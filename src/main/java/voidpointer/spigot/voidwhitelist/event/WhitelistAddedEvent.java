/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.event;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import voidpointer.spigot.voidwhitelist.Whitelistable;

@Getter
public final class WhitelistAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final Whitelistable whitelistable;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistAddedEvent(final Whitelistable whitelistable) {
        super(true);
        this.whitelistable = whitelistable;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}
