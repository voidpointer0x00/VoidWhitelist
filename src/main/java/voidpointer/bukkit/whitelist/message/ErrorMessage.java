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
public enum ErrorMessage implements Message {
    INVALID_DATE("&cYou've specified invalid date &6«{date}»&c.&r"),
    UNABLE_TO_RELOAD("&3VoidWhitelsit &8» &eUnable to reload plugin.&r"),
    ENABLED_BUT_NOT_SAVED(
        "&3VoidWhitelist &8» &eWhitelist enabled, but not saved.&r\n"
        + "&3VoidWhitelist &8» &eThe changes will not be available after server restart.&r"
    ),
    DISABLED_BUT_NOT_SAVED(
        "&3VoidWhitelist &8» &eWhitelist disabled, but not saved.&r\n"
        + "&3VoidWhitelist &8» &eThe changes will not be available after server restart.&r"
    ),
    ;

    private static final String KICK_PREFIX = "error";

    @NonNull private final String defaultValue;

    @Override public String getPath() {
        return format("%s.%s", KICK_PREFIX, toString().replace('_', '-').toLowerCase());
    }
}
