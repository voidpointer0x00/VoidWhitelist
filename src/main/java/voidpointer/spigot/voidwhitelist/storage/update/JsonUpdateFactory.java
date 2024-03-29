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
package voidpointer.spigot.voidwhitelist.storage.update;

import voidpointer.spigot.framework.localemodule.LocaleLog;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.storage.StorageVersion;

import java.util.Optional;

public final class JsonUpdateFactory {
    @AutowiredLocale private static LocaleLog log;

    public static Optional<JsonUpdate> from(final StorageVersion oldStorageVersion) {
        switch (oldStorageVersion) {
            case V1_BETA: /* this version will never be seen again (early GitHub releases only) */
                return Optional.of(new JsonV1BetaUpdate());
            case V2_BETA: /* current version */
            case UNDEFINED: /* cannot update from unknown version */
        }
        log.warn("Cannot update JSON from {0} version", oldStorageVersion);
        return Optional.empty();
    }
}
