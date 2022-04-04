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
package voidpointer.spigot.voidwhitelist.storage.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.storage.AbstractWhitelistable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class JsonWhitelistablePojo extends AbstractWhitelistable {
    @NonNull private UUID uniqueId;
    private String name;
    private Date expiresAt;
    private Date createdAt;

    @Override public Optional<Player> findAssociatedOnlinePlayer() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(this::isAssociatedWith)
                .map(player -> (Player) player)
                .findFirst();
    }

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getUniqueId().equals(uniqueId);
    }
}
