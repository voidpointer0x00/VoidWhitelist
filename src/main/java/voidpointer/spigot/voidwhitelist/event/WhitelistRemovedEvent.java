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
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import voidpointer.spigot.voidwhitelist.Whitelistable;

@Getter
public final class WhitelistRemovedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Whitelistable whitelistable;

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistRemovedEvent(final Whitelistable whitelistable) {
        this.whitelistable = whitelistable;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
