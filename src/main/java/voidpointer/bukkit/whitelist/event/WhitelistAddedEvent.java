/*
 * Copyright (c) 2020 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package voidpointer.bukkit.whitelist.event;

import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;

/**
 * Fired when new entity is added to whitelist.
 * <p>
 * The event is async by default.
 *
 * @author VoidPointer aka NyanGuyMF
 */
@Getter
public final class WhitelistAddedEvent extends Event {
    private static final boolean ASYNC = true;
    private static final HandlerList handlers = new HandlerList();

    @NonNull private final CommandSender actor;
    @NonNull private final String playerName;
    @NonNull private final Date until;

    public WhitelistAddedEvent(
            final CommandSender actor,
            final String playerName,
            final Date until
    ) {
        super(ASYNC);
        this.actor = actor;
        this.playerName = playerName;
        this.until = until;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
