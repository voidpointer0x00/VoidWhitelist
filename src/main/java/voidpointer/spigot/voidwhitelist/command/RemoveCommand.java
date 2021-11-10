package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
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
    @NonNull private final EventManager eventManager;

    public RemoveCommand(@NonNull final WhitelistService whitelistService, @NonNull final Locale locale,
                         final EventManager eventManager) {
        super(NAME);
        super.setPermission(PERMISSION);
        super.setRequiredArgsNumber(MIN_ARGS);

        this.whitelistService = whitelistService;
        this.locale = locale;
        this.eventManager = eventManager;
    }

    @Override public void execute(final Args args) {
        final String nicknameToRemove = args.get(0);

        // TODO: try async
        whitelistService.findVwPlayer(nicknameToRemove).thenAccept((vwPlayer) -> {
            if ((null == vwPlayer) || !vwPlayer.isAllowedToJoin()) {
                locale.localizeColorized(WhitelistMessage.REMOVE_NOT_WHITELISTED)
                        .set("player", nicknameToRemove)
                        .send(args.getSender());
            } else {
                whitelistService.removeFromWhitelist(vwPlayer);
                locale.localizeColorized(WhitelistMessage.REMOVED)
                        .set("player", nicknameToRemove)
                        .send(args.getSender());
            }
            eventManager.callAsyncEvent(new WhitelistRemovedEvent(nicknameToRemove));
        });

    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.size() == 1) {
            String presumptiveName = args.get(0);
            return whitelistService.getAllWhitelistedNicknames().join().stream()
                    .filter(whitelistedNickname -> whitelistedNickname.startsWith(presumptiveName))
                    .collect(Collectors.toList());
        } else if (args.size() > 1) {
            return Arrays.asList();
        }
        return whitelistService.getAllWhitelistedNicknames().join();
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localizeColorized(WhitelistMessage.REMOVE_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
