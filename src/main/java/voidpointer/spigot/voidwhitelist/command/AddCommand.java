package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.date.EssentialsDateParser;
import voidpointer.spigot.voidwhitelist.event.WhitelistAddedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public final class AddCommand extends Command {
    public static final String NAME = "add";
    public static final String PERMISSION = "whitelist.add";
    public static final int MIN_REQUIRED_ARGS = 1;

    @NonNull private final WhitelistService whitelistService;
    @NonNull private final Locale locale;

    public AddCommand(final WhitelistService whitelistService, final Locale locale) {
        super(NAME);
        this.locale = locale;

        this.whitelistService = whitelistService;
        super.setRequiredArgsNumber(MIN_REQUIRED_ARGS);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        WhitelistAddedEvent addedEvent;
        if (!hasExpiresAtArgument(args.size())) {
            addedEvent = addForever(args);
        } else {
            addedEvent = addTemporarily(args);
        }

        if (null != addedEvent)
            Bukkit.getServer().getPluginManager().callEvent(addedEvent);
    }

    @Override public List<String> tabComplete(final Args args) {
        if (args.size() == 1) {
            String presumptiveName = args.get(0);
            return Arrays.asList(Bukkit.getOfflinePlayers()).stream()
                .filter(offlinePlayer -> offlinePlayer.getName().startsWith(presumptiveName))
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
        } else if (args.size() > 1) {
            return Arrays.asList();
        }
        return Arrays.asList(Bukkit.getOfflinePlayers()).stream()
                .map(player -> player.getName())
                .collect(Collectors.toList());
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localizeColorized(WhitelistMessage.ADD_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private WhitelistAddedEvent addForever(final Args args) {
        final String nickname = args.get(0);
        whitelistService.addToWhitelist(nickname);
        locale.localizeColorized(WhitelistMessage.ADDED)
                .set("player", nickname)
                .send(args.getSender());

        return new WhitelistAddedEvent(nickname, whitelistService.NEVER_EXPIRES);
    }

    private WhitelistAddedEvent addTemporarily(final Args args) {
        final String nickname = args.get(0);
        final long whitelistTimePeriod = EssentialsDateParser.parseDate(args.get(1));

        if (EssentialsDateParser.WRONG_DATE_FORMAT == whitelistTimePeriod) {
            locale.localizeColorized(WhitelistMessage.WRONG_DATE_FORMAT).send(args.getSender());
            return null;
        }

        final Date expiresAt = new Date(whitelistTimePeriod);
        whitelistService.addToWhitelist(nickname, expiresAt);
        locale.localizeColorized(WhitelistMessage.ADDED_TEMP)
                .set("player", nickname)
                .set("time", expiresAt.toString())
                .send(args.getSender());

        return new WhitelistAddedEvent(nickname, expiresAt);
    }

    private boolean hasExpiresAtArgument(int argsNumber) {
        return MIN_REQUIRED_ARGS < argsNumber;
    }
}
