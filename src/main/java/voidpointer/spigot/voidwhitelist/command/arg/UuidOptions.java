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
package voidpointer.spigot.voidwhitelist.command.arg;

import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public enum UuidOptions implements DefinedOption {
    ONLINE, OFFLINE;
    private static final String patternFormat = "^--?%s$";

    private final Pattern pattern;

    UuidOptions() {
        pattern = compile(format(patternFormat, getName()), CASE_INSENSITIVE);
    }

    @Override public boolean matches(final CharSequence sequence) {
        return pattern.matcher(sequence).matches();
    }
}
