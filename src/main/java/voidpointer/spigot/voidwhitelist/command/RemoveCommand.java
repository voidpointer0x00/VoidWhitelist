package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveCommand extends Command {
    public static final String NAME = "remove";
    public static final String PERMISSION = "whitelist.remove";
    public static final Integer MIN_ARGS = 1;

    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Locale locale;

    public RemoveCommand(@NonNull final WhitelistService whitelistService, @NonNull final Locale locale) {
        super(NAME);
        super.setPermission(PERMISSION);
        super.setRequiredArgsNumber(MIN_ARGS);

        this.whitelistService = whitelistService;
        this.locale = locale;
    }

    @Override public void execute(final Args args) {
        final String nicknameToRemove = args.get(0);

        if (!whitelistService.isWhitelisted(nicknameToRemove)) {
            locale.localizeColorized(WhitelistMessage.REMOVE_NOT_WHITELISTED)
                    .set("player", nicknameToRemove)
                    .send(args.getSender());
            return;
        }

        whitelistService.removeFromWhitelist(nicknameToRemove);
        locale.localizeColorized(WhitelistMessage.REMOVED)
                .set("player", nicknameToRemove)
                .send(args.getSender());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(nicknameToRemove)) {
                player.kickPlayer(locale.localizeColorized(WhitelistMessage.YOU_WERE_REMOVED).getRawMessage());
                break;
            }
        }
    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.size() == 1) {
            String presumptiveName = args.get(0);
            return whitelistService.getWhitelistedNicknames().stream()
                    .filter(whitelistedNickname -> whitelistedNickname.startsWith(presumptiveName))
                    .collect(Collectors.toList());
        } else if (args.size() > 1) {
            return Arrays.asList();
        }
        return whitelistService.getWhitelistedNicknames();
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localizeColorized(WhitelistMessage.REMOVE_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
