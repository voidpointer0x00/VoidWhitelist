package voidpointer.spigot.voidwhitelist.storage.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.storage.AbstractWhitelistableName;

import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
@NoArgsConstructor
@AllArgsConstructor
final class JsonWhitelistableNamePojo extends AbstractWhitelistableName {
    @NonNull
    @EqualsAndHashCode.Include
    private String name;
    private Date expiresAt;

    @Override public Optional<Player> findAssociatedOnlinePlayer() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getName().equals(name))
                .map(player -> (Player) player)
                .findFirst();
    }
}
