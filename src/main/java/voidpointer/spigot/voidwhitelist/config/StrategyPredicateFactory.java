package voidpointer.spigot.voidwhitelist.config;

import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.function.Predicate;

final class StrategyPredicateFactory {
    static final Strategy DEFAULT_STRATEGY = Strategy.NEWCOMERS;
    private enum Strategy {ALL, NEWCOMERS}

    static Predicate<UUID> getPredicate(final String strategyName) {
        switch (strategyForName(strategyName)) {
            case ALL:
                return uuid -> true;
            case NEWCOMERS:
            default:
                return uuid -> Bukkit.getOfflinePlayer(uuid).hasPlayedBefore();
        }
    }

    private static Strategy strategyForName(final String strategyName) {
        try {
            return Strategy.valueOf(strategyName.toUpperCase());
        } catch (final IllegalArgumentException unknownStrategyName) {
            return DEFAULT_STRATEGY;
        }
    }
}
