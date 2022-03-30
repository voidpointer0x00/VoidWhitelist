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
package voidpointer.spigot.voidwhitelist.uuid;

import be.seeseemelk.mockbukkit.MockBukkit;
import manifold.ext.rt.api.Jailbreak;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.voidwhitelist.VoidWhitelistPlugin;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UUIDFetcherTest {
    private static VoidWhitelistPlugin plugin;
    private static LocaleLog localeLog;

    @BeforeAll static void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(VoidWhitelistPlugin.class);
    }

    @AfterAll static void tearUp() {
        MockBukkit.unmock();
    }

    static Stream<Arguments> getOnlineFetcher() {
        return Stream.of(
                arguments((UUIDFetcher)OnlineUUIDFetcher::getUUID, "_voidpointer", "c55a15b5-896f-4c09-9c07-75ad36572aad"),
                arguments((UUIDFetcher)OnlineUUIDFetcher::getUUID, "Notch", "069a79f4-44e9-4726-a5be-fca90e38aaf5"),
                arguments((UUIDFetcher)OnlineUUIDFetcher::getUUID, "Dinnerbone", "61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
                arguments((UUIDFetcher)OnlineUUIDFetcher::getUUID, "slicedlime", "9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
                arguments((UUIDFetcher)OnlineUUIDFetcher::getUUID, "ilmango", "52ea9354-99ed-4b06-bec2-331e7c0f6f57")
        );
    }

    static Stream<Arguments> getOfflineFetcher() {
        return Stream.of(
                arguments((UUIDFetcher)OfflineUUIDFetcher::getUUID, "_voidpointer", "3d6e6616-8029-3e86-8d40-8b5bbbd0cc2a"),
                arguments((UUIDFetcher)OfflineUUIDFetcher::getUUID, "Notch", "b50ad385-829d-3141-a216-7e7d7539ba7f"),
                arguments((UUIDFetcher)OfflineUUIDFetcher::getUUID, "Dinnerbone", "4d258a81-2358-3084-8166-05b9faccad80"),
                arguments((UUIDFetcher)OfflineUUIDFetcher::getUUID, "slicedlime", "0310df31-0906-37c1-b92c-231dd867f622"),
                arguments((UUIDFetcher)OfflineUUIDFetcher::getUUID, "ilmango", "67d8f00e-ec19-3a06-9747-11f67788288a")
        );
    }

    @ParameterizedTest
    @MethodSource({"getOnlineFetcher", "getOfflineFetcher"})
    void testGetUUID(UUIDFetcher uuidFetcher, String name, String expect) {
        CompletableFuture<Optional<UUID>> uuid = uuidFetcher.getUUID(name);
        assertNotNull(uuid);
        assertNotNull(uuid.join());
        assertDoesNotThrow(() -> {
            assertNotNull(uuid.get());
            assertTrue(uuid.get().isPresent());
            assertEquals(expect, uuid.get().get().toString());
        });
    }

    @ParameterizedTest
    @MethodSource("getIdUuid")
    void testIdToUuid(String id, String expect) {
        @Jailbreak OnlineUUIDFetcher uuidFetcher = new OnlineUUIDFetcher();
        assertEquals(expect, uuidFetcher.idToUuid(id));
    }

    static Stream<Arguments> getIdUuid() {
        return Stream.of(
                arguments("c55a15b5896f4c099c0775ad36572aad", "c55a15b5-896f-4c09-9c07-75ad36572aad"),
                arguments("069a79f444e94726a5befca90e38aaf5", "069a79f4-44e9-4726-a5be-fca90e38aaf5"),
                arguments("61699b2ed3274a019f1e0ea8c3f06bc6", "61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
                arguments("9c2ac9585de945a88ca14122eb4c0b9e", "9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
                arguments("52ea935499ed4b06bec2331e7c0f6f57", "52ea9354-99ed-4b06-bec2-331e7c0f6f57"),
                arguments("3d6e661680293e868d408b5bbbd0cc2a", "3d6e6616-8029-3e86-8d40-8b5bbbd0cc2a"),
                arguments("b50ad385829d3141a2167e7d7539ba7f", "b50ad385-829d-3141-a216-7e7d7539ba7f"),
                arguments("4d258a8123583084816605b9faccad80", "4d258a81-2358-3084-8166-05b9faccad80"),
                arguments("0310df31090637c1b92c231dd867f622", "0310df31-0906-37c1-b92c-231dd867f622"),
                arguments("67d8f00eec193a06974711f67788288a", "67d8f00e-ec19-3a06-9747-11f67788288a")
        );
    }
}