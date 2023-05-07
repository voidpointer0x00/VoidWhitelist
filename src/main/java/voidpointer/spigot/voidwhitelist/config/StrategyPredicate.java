package voidpointer.spigot.voidwhitelist.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum StrategyPredicate implements Predicate<UUID> {
    ALL(uuid -> true), NEWCOMERS(uuid -> !Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()),
    ;

    public static final StrategyPredicate DEFAULT_STRATEGY = ALL;

    public static Optional<StrategyPredicate> of(final String strategyName) {
        try {
            return Optional.of(StrategyPredicate.valueOf(strategyName.toUpperCase()));
        } catch (final IllegalArgumentException unknownStrategy) {
            return Optional.empty();
        }
    }

    static StrategyPredicate getOrDefault(final String strategyName) {
        return of(strategyName).orElse(DEFAULT_STRATEGY);
    }

    @NotNull private final Predicate<UUID> uuidStrategyPredicate;

    @Override public boolean test(final UUID uuid) {
        return uuidStrategyPredicate.test(uuid);
    }

    public String getName() {
        return toString().toLowerCase().replace('_', '-');
    }
}
