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
package voidpointer.spigot.voidwhitelist.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class UuidOptionsTest {

    @ParameterizedTest
    @MethodSource({"onlineMatches", "onlineDoesNotMatch", "offlineMatches", "offlineDoesNotMatch"})
    void testGetPattern(final ArgOption option, final String arg, final boolean expected) {
        Assertions.assertEquals(expected, option.matches(arg));
    }

    static Stream<Arguments> onlineMatches() {
        return Stream.of(
                arguments(UuidOptions.ONLINE, "-online", true),
                arguments(UuidOptions.ONLINE, "-ONLINE", true),
                arguments(UuidOptions.ONLINE, "-OnLiNe", true),
                arguments(UuidOptions.ONLINE, "-oNlInE", true),
                arguments(UuidOptions.ONLINE, "-onlINE", true),
                arguments(UuidOptions.ONLINE, "-ONLine", true),

                arguments(UuidOptions.ONLINE, "--online", true),
                arguments(UuidOptions.ONLINE, "--ONLINE", true),
                arguments(UuidOptions.ONLINE, "--OnLiNe", true),
                arguments(UuidOptions.ONLINE, "--oNlInE", true),
                arguments(UuidOptions.ONLINE, "--onlINE", true),
                arguments(UuidOptions.ONLINE, "--ONLine", true)
        );
    }

    static Stream<Arguments> onlineDoesNotMatch() {
        return Stream.of(
                arguments(UuidOptions.ONLINE, "online", false),
                arguments(UuidOptions.ONLINE, ".online", false),
                arguments(UuidOptions.ONLINE, ".-online", false),
                arguments(UuidOptions.ONLINE, ".--online", false),
                arguments(UuidOptions.ONLINE, "online.", false),
                arguments(UuidOptions.ONLINE, "-online.", false),
                arguments(UuidOptions.ONLINE, "--online.", false),

                arguments(UuidOptions.ONLINE, "offline", false),
                arguments(UuidOptions.ONLINE, ".offline", false),
                arguments(UuidOptions.ONLINE, ".-offline", false),
                arguments(UuidOptions.ONLINE, ".--offline", false),
                arguments(UuidOptions.ONLINE, "offline.", false),
                arguments(UuidOptions.ONLINE, "-offline.", false),
                arguments(UuidOptions.ONLINE, "--offline.", false)
        );
    }

    static Stream<Arguments> offlineMatches() {
        return Stream.of(
                arguments(UuidOptions.OFFLINE, "-offline", true),
                arguments(UuidOptions.OFFLINE, "-OFFLINE", true),
                arguments(UuidOptions.OFFLINE, "-OfFlInE", true),
                arguments(UuidOptions.OFFLINE, "-oFfLiNe", true),
                arguments(UuidOptions.OFFLINE, "-OFFline", true),
                arguments(UuidOptions.OFFLINE, "-offLINE", true),

                arguments(UuidOptions.OFFLINE, "--offline", true),
                arguments(UuidOptions.OFFLINE, "--OFFLINE", true),
                arguments(UuidOptions.OFFLINE, "--OfFlInE", true),
                arguments(UuidOptions.OFFLINE, "--OFFline", true),
                arguments(UuidOptions.OFFLINE, "--offLINE", true)
        );
    }

    static Stream<Arguments> offlineDoesNotMatch() {
        return Stream.of(
                arguments(UuidOptions.OFFLINE, "offline", false),
                arguments(UuidOptions.OFFLINE, ".offline", false),
                arguments(UuidOptions.OFFLINE, ".-offline", false),
                arguments(UuidOptions.OFFLINE, ".--offline", false),
                arguments(UuidOptions.OFFLINE, "offline.", false),
                arguments(UuidOptions.OFFLINE, "-offline.", false),
                arguments(UuidOptions.OFFLINE, "--offline.", false),

                arguments(UuidOptions.OFFLINE, "online", false),
                arguments(UuidOptions.OFFLINE, ".online", false),
                arguments(UuidOptions.OFFLINE, ".-online", false),
                arguments(UuidOptions.OFFLINE, ".--online", false),
                arguments(UuidOptions.OFFLINE, "online.", false),
                arguments(UuidOptions.OFFLINE, "-online.", false),
                arguments(UuidOptions.OFFLINE, "--online.", false)
        );
    }
}