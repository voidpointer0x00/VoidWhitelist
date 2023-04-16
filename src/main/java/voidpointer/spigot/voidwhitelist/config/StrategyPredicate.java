package voidpointer.spigot.voidwhitelist.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum StrategyPredicate implements Predicate<UUID> {
    ALL(uuid -> true), NEWCOMERS(uuid -> !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()),
    ;

    public static final StrategyPredicate DEFAULT_STRATEGY = ALL;

    static StrategyPredicate of(final String strategyName) {
        try {
            return StrategyPredicate.valueOf(strategyName.toLowerCase());
        } catch (final IllegalArgumentException unknownStrategy) {
            return DEFAULT_STRATEGY;
        }
    }

    @NotNull private final Predicate<UUID> uuidStrategyPredicate;

    @Override public boolean test(final UUID uuid) {
        return uuidStrategyPredicate.test(uuid);
    }

    public String getName() {
        return toString().toLowerCase().replace('_', '-');
    }
}
