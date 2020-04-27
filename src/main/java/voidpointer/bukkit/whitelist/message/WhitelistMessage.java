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
package voidpointer.bukkit.whitelist.message;

import static java.lang.String.format;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import voidpointer.bukkit.framework.locale.Message;

/** @author VoidPointer aka NyanGuyMF */
@Getter
@RequiredArgsConstructor
public enum WhitelistMessage implements Message {
    WHITELIST_UNKNOWN_PLAYER("&eWarning! Adding unknown player &6«&c{player}&6» &cto whitelist."),
    PLAYER_ADDED("&ePlayer &6«&c{player}&6» &eadded to whitelist.&r"),
    PLAYER_ADDED_UNTIL("&ePlayer &6«&c{player}&6» &eadded to whitelist until &d{until}&e.&r"),
    UNTIL_FORMAT("{day}.{month}.{year} {hour}:{minute}:{second}.{millisecond}"),
    PLAYER_NOT_WHITELISTED("&ePlayer with nickname &6«&c{player}&6» &eisn't whitelisted.&r"),
    PLAYER_REMOVED("&ePlayer &6«&c{player}&6» &eis not whitelisted anymore.&r"),
    ;

    private static final String KICK_PREFIX = "whitelist";

    @NonNull private final String defaultValue;

    @Override public String getPath() {
        return format("%s.%s", KICK_PREFIX, toString().replace('_', '-').toLowerCase());
    }
}
