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
import voidpointer.spigot.voidwhitelist.command.ImportJsonCommand;

import java.util.Set;

/**
 * <p>Called asynchronously when someone imported a whitelist storage.</p>
 *
 * <p>Import actions do not call {@link WhitelistAddedEvent} for any
 *      imported entity.</p>
 *
 * @see ImportJsonCommand
 */
public final class WhitelistImportEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * An unmodifiable {@link Set} of imported {@link Whitelistable} entities.
     */
    private @NonNull @Getter final Set<Whitelistable> imported;

    public WhitelistImportEvent(final @NonNull Set<Whitelistable> imported) {
        super(true);
        this.imported = imported;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }
}
