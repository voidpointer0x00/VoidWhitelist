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
package voidpointer.spigot.voidwhitelist.gui;

import voidpointer.spigot.voidwhitelist.net.Profile;

import java.util.Optional;

import static java.util.UUID.nameUUIDFromBytes;

final class ControlSkulls {
    private static final String cyanForwardBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNmYWJjMzg5MWM5NjJjOTEzOTU5ZDY1YmU5MGRhYTVkZTEyMTFmYTQwODdkYWUzODZmZjRlZDQyNDhmIn19fQ==";
    private static final String cyanBackBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE0NTljZmM0NGNjNTFkZGYyNzk4ODU5NmQyZGU4YWM4NTU2ZTkzZDc5NDYyMTljZjY0YzkwYzhjMDVmY2EifX19";
    private static final String disabledBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NjNDcwYWUyNjMxZWZkZmFmOTY3YjM2OTQxM2JjMjQ1MWNkN2EzOTQ2NWRhNzgzNmE2YzdhMTRlODc3In19fQ==";
    private static final String enabledBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI5NmQzZTE0OTNmYTMyZDgyN2EzNjM1YTY4M2U1YmRlZDY0OTE0ZDc1ZTczYWFjZGNjYmE0NmQ4ZmQ5MCJ9fX0=";
    private static final String whiteRefreshBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDc1ZDNkYjAzZGMyMWU1NjNiMDM0MTk3ZGE0MzViNzllY2ZlZjRiOGUyZWNkYjczMGUzNzBjMzE2NjI5ZDM2ZiJ9fX0=";

    public static ProfileSkull getForward() {
        return skullOf(cyanForwardBase64);
    }

    public static ProfileSkull getBack() {
        return skullOf(cyanBackBase64);
    }

    public static ProfileSkull getEnabled() {
        return skullOf(enabledBase64);
    }

    public static ProfileSkull getDisabled() {
        return skullOf(disabledBase64);
    }

    public static ProfileSkull getRefresh() {
        return skullOf(whiteRefreshBase64);
    }

    private static ProfileSkull skullOf(final String base64) {
        return ProfileSkull.of(new Profile(nameUUIDFromBytes(base64.getBytes()), null, Optional.of(base64)));
    }
}
