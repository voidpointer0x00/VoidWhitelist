package voidpointer.spigot.voidwhitelist.storage;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.WhitelistableUUID;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
public final class SimpleWhitelistUUID extends AbstractWhitelistable implements WhitelistableUUID {
    @NonNull
    @EqualsAndHashCode.Include
    private UUID uniqueId;
    private Date expiresAt;

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getUniqueId().equals(uniqueId);
    }

    @Override public String toString() {
        return uniqueId.toString();
    }
}
