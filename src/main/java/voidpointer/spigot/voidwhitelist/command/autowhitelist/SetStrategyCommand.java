package voidpointer.spigot.voidwhitelist.command.autowhitelist;

import com.google.common.base.Joiner;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.config.StrategyPredicate;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;

final class SetStrategyCommand extends Command {
    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistConfig whitelistConfig;

    SetStrategyCommand() {
        super("set-strategy");

        super.setRequiredArgsNumber(1);
    }

    @Override public void execute(final Args args) {
        Optional<StrategyPredicate> strategy = StrategyPredicate.of(args.get(0));
        if (strategy.isEmpty()) {
            locale.localize(AUTO_WHITELIST_SET_INVALID_STRATEGY)
                    .set("given", args.get(0)).set("strategies", Joiner.on(", ").join(StrategyPredicate.values()))
                    .send(args.getSender());
            return;
        }
        final StrategyPredicate previousStrategy = whitelistConfig.setAutoWhitelistStrategy(strategy.get());
        locale.localize(AUTO_WHITELIST_SET_STRATEGY)
                .set("old", previousStrategy.getName()).set("new", strategy.get().getName())
                .send(args.getSender());
    }

    @Override public List<String> tabComplete(final Args args) {
        return switch (args.size()) {
            case 0 -> Arrays.stream(StrategyPredicate.values())
                    .map(StrategyPredicate::getName)
                    .collect(Collectors.toList());
            case 1 -> Arrays.stream(StrategyPredicate.values())
                    .map(StrategyPredicate::getName)
                    .filter(strategyName -> strategyName.startsWith(args.get(0)))
                    .collect(Collectors.toList());
            default -> Collections.emptyList();
        };
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(AUTO_WHITELIST_SET_STRATEGY_ARGS).send(args.getSender());
    }
}
