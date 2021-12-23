package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.VwPlayer;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class IsWhitelistedCommand extends Command {
    public static final String NAME = "iswhitelisted";
    public static final List<String> ALIASES = Collections.singletonList("is-whitelisted");
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
        if (!args.isPlayer() && (0 == args.size())) {
            locale.localizeColorized(WhitelistMessage.CONSOLE_WHITELISTED).send(args.getSender());
            return;
        }

        final String nicknameToCheck = (0 == args.size()) ? args.getSender().getName() : args.get(0);
        final VwPlayer vwPlayer = whitelistService.findVwPlayer(nicknameToCheck).join();
        if ((null == vwPlayer) || !vwPlayer.isAllowedToJoin()) {
            tellNotWhitelisted(args.getSender(), nicknameToCheck);
        } else if (vwPlayer.isExpirable()) {
            tellWhitelistedTemporarily(args.getSender(), nicknameToCheck, vwPlayer.getExpiresAt());
        } else {
            tellWhitelisted(args.getSender(), nicknameToCheck);
        }
    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private void tellNotWhitelisted(final CommandSender sender, final String playerName) {
        locale.localizeColorized(WhitelistMessage.INFO_NOT_WHITELISTED)
                .set("player", playerName)
                .send(sender);
    }

    private void tellWhitelistedTemporarily(final CommandSender sender, final String playerName,
                                            final Date expiresAt) {
        locale.localizeColorized(WhitelistMessage.INFO_WHITELISTED_TEMP)
                .set("player", playerName)
                .set("time", expiresAt.toString())
                .send(sender);
    }

    private void tellWhitelisted(final CommandSender sender, final String playerName) {
        locale.localizeColorized(WhitelistMessage.INFO_WHITELISTED)
                .set("player", playerName)
                .send(sender);
    }
}
