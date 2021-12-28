package voidpointer.spigot.voidwhitelist.storage;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import voidpointer.spigot.voidwhitelist.WhitelistableName;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded=true)
public final class SimpleWhitelistableName extends AbstractWhitelistable implements WhitelistableName {
    @NonNull
    @EqualsAndHashCode.Include
    private final String name;
    private Date expiresAt;

    @Override public boolean isAssociatedWith(final Player player) {
        return player.getName().equals(name);
    }

    @Override public String toString() {
        return name;
    }
}
