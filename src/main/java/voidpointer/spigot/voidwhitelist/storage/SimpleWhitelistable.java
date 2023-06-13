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
package voidpointer.spigot.voidwhitelist.storage;

import lombok.*;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public final class SimpleWhitelistable extends AbstractWhitelistable {
    @NonNull
    @EqualsAndHashCode.Include
    private UUID uniqueId;
    @NonNull
    private String name;
    private Date expiresAt;

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getUniqueId().equals(uniqueId);
    }

    @Override public String toString() {
        return uniqueId.toString();
    }
}
