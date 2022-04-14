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
import org.checkerframework.checker.nullness.qual.NonNull;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.command.AddCommand;

/**
 * <p>Called asynchronously when someone adds a {@link Whitelistable} entity.</p>
 *
 * <p><b>Notice:</b> this event doesn't get called if someone imported the whitelist.</p>
 *
 * @see AddCommand
 * @see WhitelistImportEvent
 */
public final class WhitelistAddedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /** The added {@link Whitelistable} entity. */
    private @Getter @NonNull final Whitelistable whitelistable;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WhitelistAddedEvent(final @NonNull Whitelistable whitelistable) {
        super(true);
        this.whitelistable = whitelistable;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }
}
