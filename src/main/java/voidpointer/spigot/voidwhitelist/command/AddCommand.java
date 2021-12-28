package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.date.EssentialsDateParser;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public final class AddCommand extends Command {
    public static final String NAME = "add";
    public static final String PERMISSION = "whitelist.add";
    public static final int MIN_REQUIRED_ARGS = 1;

    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Locale locale;
    @NonNull private final EventManager eventManager;

    public AddCommand(final WhitelistService whitelistService, final Locale locale,
                      final EventManager eventManager) {
        super(NAME);
        this.locale = locale;
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);
        super.setPermission(PERMISSION);

        this.whitelistService = whitelistService;
        this.eventManager = eventManager;
    }

    @Override public void execute(final Args args) {
        if (!hasExpiresAtArgument(args.size()))
            addForever(args);
        else
            addTemporarily(args);
    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.size() == 1) {
            String presumptiveName = args.get(0);
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(offlinePlayer -> (null != offlinePlayer.getName())
                            && offlinePlayer.getName().startsWith(presumptiveName))
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        } else if (args.size() > 1) {
            return Collections.emptyList();
        }
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localizeColorized(WhitelistMessage.ADD_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private void addForever(final Args args) {
        final String nickname = args.get(0);
        whitelistService.add(nickname).thenAcceptAsync(this::callWhitelistAddedEvent);
        locale.localizeColorized(WhitelistMessage.ADDED)
                .set("player", nickname)
                .send(args.getSender());
    }

    private void addTemporarily(final Args args) {
        final String nickname = args.get(0);
        final long whitelistTimePeriod = EssentialsDateParser.parseDate(args.get(1));

        if (EssentialsDateParser.WRONG_DATE_FORMAT == whitelistTimePeriod) {
            locale.localizeColorized(WhitelistMessage.WRONG_DATE_FORMAT).send(args.getSender());
            return;
        }

        final Date expiresAt = new Date(whitelistTimePeriod);
        whitelistService.add(nickname, expiresAt).thenAcceptAsync(this::callWhitelistAddedEvent);
        locale.localizeColorized(WhitelistMessage.ADDED_TEMP)
                .set("player", nickname)
                .set("time", expiresAt.toString())
                .send(args.getSender());
    }

    private void callWhitelistAddedEvent(final Whitelistable whitelistable) {
        eventManager.callAsyncEvent(new WhitelistAddedEvent(whitelistable));
    }

    private boolean hasExpiresAtArgument(int argsNumber) {
        return MIN_REQUIRED_ARGS < argsNumber;
    }
}
