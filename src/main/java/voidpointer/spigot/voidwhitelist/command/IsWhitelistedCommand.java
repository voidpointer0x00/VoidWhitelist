package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class IsWhitelistedCommand extends Command {
    public static final String NAME = "iswhitelisted";
    public static final List<String> ALIASES = Arrays.asList("is-whitelisted");
    public static final String PERMISSION = "whitelist.iswhitelisted";

    @NonNull private final Locale locale;
    @NonNull private final WhitelistService whitelistService;

    public IsWhitelistedCommand(WhitelistService whitelistService, Locale locale) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.locale = locale;
        this.whitelistService = whitelistService;
    }

    @Override public void execute(final Args args) {
        /* the code below can be improved if you use one (0 == args.size()) if
        * comparison less, but it will entail nesting, increase overall complexity,
        * and as a result reduce readability. (i've no clue why u're even reading this honesty) */
        if (!args.isPlayer() && (0 == args.size())) {
            locale.localizeColorized(WhitelistMessage.CONSOLE_WHITELISTED).send(args.getSender());
            return;
        }

        final String nicknameToCheck = (0 == args.size()) ? args.getSender().getName() : args.get(0);
        if (!whitelistService.isWhitelisted(nicknameToCheck)) {
            locale.localizeColorized(WhitelistMessage.INFO_NOT_WHITELISTED)
                .set("player", nicknameToCheck)
                .send(args.getSender());
            return;
        }

        final Date expiresAt = whitelistService.getExpiresAt(nicknameToCheck);
        if (null != expiresAt) {
            locale.localizeColorized(WhitelistMessage.INFO_WHITELISTED_TEMP)
                    .set("player", nicknameToCheck)
                    .set("time", expiresAt.toString())
                    .send(args.getSender());
        } else {
            locale.localizeColorized(WhitelistMessage.INFO_WHITELISTED)
                    .set("player", nicknameToCheck)
                    .send(args.getSender());
        }
    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
