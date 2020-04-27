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
public enum InfoMessage implements Message {
    RELOADED("&3VoidWhitelist &8» &ePlugin reloaded in &a{ms}&ems.&r"),
    ENABLED_AND_SAVED("&3VoidWhitelist &8» &eWhitelist enabled and saved.&r"),
    DISABLED_AND_SAVED("&3VoidWhitelist &8» &eWhitelist disabled and saved.&r"),
    INFO_NOT_WHITELISTED(
        "&ePlayer &6«&c{player}&6»&e:\n"
        + " &eWhitelisted: &cnot."
    ),
    INFO_WHITELISTED(
        "&ePlayer &6«&c{player}&6»&e:\n"
        + " &eWhitelisted: &eyes"
    ),
    INFO_WHITELISTED_UNTIL(
        "&ePlayer &6«&c{player}&6»&e:\n"
        + " &eWhitelisted: &eyes&e, till &d{until}&e."
    ),
    INFO_EXPIRED(
        "&ePlayer &6«&c{player}&6»&e:\n"
        + " &eWhitelisted: &eexpired at &d{until}&e."
    ),
    ;

    private static final String KICK_PREFIX = "info";

    @NonNull private final String defaultValue;

    @Override public String getPath() {
        return format("%s.%s", KICK_PREFIX, toString().replace('_', '-').toLowerCase());
    }
}
