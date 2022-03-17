package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.List;

public final class WhitelistCommand extends Command {
    public static final String NAME = "whitelist";
    public static final int MIN_REQUIRED_ARGS = 1;

    @NonNull private final CommandManager whitelistCommands = new CommandManager();
    @NonNull private final Locale locale;

    public WhitelistCommand(@NonNull final Locale locale, @NonNull final WhitelistService whitelistService,
                            @NonNull final WhitelistConfig whitelistConfig, final EventManager eventManager) {
        super(NAME);
        this.locale = locale;

        whitelistCommands.addCommand(new AddCommand(whitelistService, locale, eventManager));
        whitelistCommands.addCommand(new RemoveCommand(whitelistService, locale, eventManager));
        whitelistCommands.addCommand(new EnableCommand(whitelistConfig, locale, eventManager));
        whitelistCommands.addCommand(new DisableCommand(whitelistConfig, locale, eventManager));
        whitelistCommands.addCommand(new InfoCommand(whitelistService, locale));
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);
    }

    @Override public void execute(final Args args) {
        final String subCommandName = args.get(0);

        args.getArgs().removeFirst();
        try {
            whitelistCommands.executeCommand(subCommandName, args);
        } catch (IllegalArgumentException illegalArgumentException) {
            locale.localizeColorized(WhitelistMessage.WHITELIST_HELP).send(args.getSender());
        }
    }

    @Override public List<String> tabComplete(final Args args) {
        return whitelistCommands.tabComplete(args);
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localizeColorized(WhitelistMessage.WHITELIST_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
