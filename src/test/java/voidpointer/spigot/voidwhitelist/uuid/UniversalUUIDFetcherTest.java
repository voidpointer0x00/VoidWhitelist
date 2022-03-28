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

class UniversalUUIDFetcherTest {
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
                arguments(new UniversalUUIDFetcher(true), "_voidpointer", "c55a15b5-896f-4c09-9c07-75ad36572aad"),
                arguments(new UniversalUUIDFetcher(true), "Notch", "069a79f4-44e9-4726-a5be-fca90e38aaf5"),
                arguments(new UniversalUUIDFetcher(true), "Dinnerbone", "61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"),
                arguments(new UniversalUUIDFetcher(true), "slicedlime", "9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e"),
                arguments(new UniversalUUIDFetcher(true), "ilmango", "52ea9354-99ed-4b06-bec2-331e7c0f6f57")
        );
    }

    static Stream<Arguments> getOfflineFetcher() {
        return Stream.of(
                arguments(new UniversalUUIDFetcher(false), "_voidpointer", "3d6e6616-8029-3e86-8d40-8b5bbbd0cc2a"),
                arguments(new UniversalUUIDFetcher(false), "Notch", "b50ad385-829d-3141-a216-7e7d7539ba7f"),
                arguments(new UniversalUUIDFetcher(false), "Dinnerbone", "4d258a81-2358-3084-8166-05b9faccad80"),
                arguments(new UniversalUUIDFetcher(false), "slicedlime", "0310df31-0906-37c1-b92c-231dd867f622"),
                arguments(new UniversalUUIDFetcher(false), "ilmango", "67d8f00e-ec19-3a06-9747-11f67788288a")
        );
    }

    @ParameterizedTest
    @MethodSource({"getOnlineFetcher", "getOfflineFetcher"})
    void testGetUUID(UniversalUUIDFetcher uuidFetcher, String name, String expect) {
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
    @MethodSource("getOnlineFetcher")
    void testGetOnlineUUID(UniversalUUIDFetcher uuidFetcher, String name, String expect) {
        CompletableFuture<Optional<UUID>> uuid = uuidFetcher.getOnlineUUID(name);
        assertNotNull(uuid);
        assertNotNull(uuid.join());
        assertDoesNotThrow(() -> {
            assertNotNull(uuid.get());
            assertTrue(uuid.get().isPresent());
            assertEquals(expect, uuid.get().get().toString());
        });
    }

    @ParameterizedTest
    @MethodSource("getOfflineFetcher")
    void testGetOfflineUUID(UniversalUUIDFetcher uuidFetcher, String name, String expect) {
        UUID uuid = uuidFetcher.getOfflineUUID(name);
        assertNotNull(uuid);
        assertEquals(expect, uuid.toString());
    }

    @ParameterizedTest
    @MethodSource("getIdUuid")
    void testIdToUuid(String id, String expect) {
        @Jailbreak UniversalUUIDFetcher uuidFetcher = new UniversalUUIDFetcher(false);
        assertEquals(expect, uuidFetcher.idToUuid(id));
    }

    static Stream<Arguments> getIdUuid() {
        return Stream.of(
                arguments("c55a15b5896f4c099c0775ad36572aad", "c55a15b5-896f-4c09-9c07-75ad36572aad")
        );
    }
}