package voidpointer.spigot.voidwhitelist.storage.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
final class JsonWhitelistableNamePojo extends AbstractWhitelistable {
    @NonNull
    @EqualsAndHashCode.Include
    private UUID uniqueId;
    private Date expiresAt;

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
