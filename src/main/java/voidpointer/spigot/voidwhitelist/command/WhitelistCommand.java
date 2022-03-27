package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.util.List;

public final class WhitelistCommand extends Command {
    public static final String NAME = "whitelist";
    public static final int MIN_REQUIRED_ARGS = 1;

    @AutowiredLocale private static Locale locale;
    @NonNull private final CommandManager whitelistCommands = new CommandManager();

    public WhitelistCommand(@NonNull final WhitelistService whitelistService,
                            @NonNull final WhitelistConfig whitelistConfig,
                            @NonNull final EventManager eventManager,
                            @NonNull final UUIDFetcher uniqueIdFetcher) {
        super(NAME);

        whitelistCommands.addCommand(new AddCommand(whitelistService, eventManager, uniqueIdFetcher));
        whitelistCommands.addCommand(new RemoveCommand(whitelistService, eventManager, uniqueIdFetcher));
        whitelistCommands.addCommand(new EnableCommand(whitelistConfig, eventManager));
        whitelistCommands.addCommand(new DisableCommand(whitelistConfig, eventManager));
        whitelistCommands.addCommand(new InfoCommand(whitelistService, uniqueIdFetcher));
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);
    }

    @Override public void execute(final Args args) {
        final String subCommandName = args.get(0);

        args.getArgs().removeFirst();
        try {
            whitelistCommands.executeCommand(subCommandName, args);
        } catch (IllegalArgumentException illegalArgumentException) {
            locale.localize(WhitelistMessage.WHITELIST_HELP).send(args.getSender());
        }
    }

    @Override public List<String> tabComplete(final Args args) {
        return whitelistCommands.tabComplete(args);
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localize(WhitelistMessage.WHITELIST_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
