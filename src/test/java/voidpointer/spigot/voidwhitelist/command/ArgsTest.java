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

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArgsTest {
    private static final Collection<ArgOption> options = asList(UuidOptions.ONLINE, UuidOptions.OFFLINE);
    private static ConsoleCommandSender sender;

    @BeforeAll static void setUp() {
        MockBukkit.mock();
        sender = MockBukkit.getMock().getConsoleSender();
    }

    @BeforeAll static void tearUp() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @MethodSource("testParseOptionsSource")
    void testParseOptions(Args args, Collection<ArgOption> expectedOptions, Collection<String> expectedArgs) {
        args.parseOptions(options);
        assertTrue(collectionsEquals(expectedOptions, args.getOptions()));
        assertTrue(collectionsEquals(args.getArgs(), expectedArgs));
    }

    static Stream<Arguments> testParseOptionsSource() {
        return Stream.of(
                arguments(new Args(sender, new String[] {"player","online"}), Collections.EMPTY_LIST,
                        asList("player","online")),
                arguments(new Args(sender, new String[] {"player","-online"}), singletonList(UuidOptions.ONLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","--online"}), singletonList(UuidOptions.ONLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","-oNlInE"}), singletonList(UuidOptions.ONLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","--oNlInE"}), singletonList(UuidOptions.ONLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","offline"}), Collections.EMPTY_LIST,
                        asList("player","offline")),
                arguments(new Args(sender, new String[] {"player","-offline"}), singletonList(UuidOptions.OFFLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","--offline"}), singletonList(UuidOptions.OFFLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","-oFfLiNe"}), singletonList(UuidOptions.OFFLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","--oFfLiNe"}), singletonList(UuidOptions.OFFLINE),
                        singletonList("player")),
                arguments(new Args(sender, new String[] {"player","-offline","-online"}),
                        asList(UuidOptions.ONLINE, UuidOptions.OFFLINE), singletonList("player")),
                arguments(new Args(sender, new String[] {"player","--offline","-online"}),
                        asList(UuidOptions.ONLINE, UuidOptions.OFFLINE), singletonList("player")),
                arguments(new Args(sender, new String[] {"player","-offline","--online"}),
                        asList(UuidOptions.ONLINE, UuidOptions.OFFLINE), singletonList("player")),
                arguments(new Args(sender, new String[] {"player","--offline","--online"}),
                        asList(UuidOptions.ONLINE, UuidOptions.OFFLINE), singletonList("player"))
        );
    }

    static boolean collectionsEquals(Collection<?> a, Collection<?> b) {
        return a.size() == b.size() && a.containsAll(b) && b.containsAll(a);
    }
}