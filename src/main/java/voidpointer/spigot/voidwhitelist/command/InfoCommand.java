package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public final class InfoCommand extends Command {
    public static final String NAME = "info";
    public static final String PERMISSION = "whitelist.info";

    @NonNull private final Locale locale;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final UUIDFetcher uniqueIdFetcher;

    public InfoCommand(WhitelistService whitelistService, Locale locale, final @NonNull UUIDFetcher uniqueIdFetcher) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.locale = locale;
        this.whitelistService = whitelistService;
        this.uniqueIdFetcher = uniqueIdFetcher;
    }

    @Override public void execute(final Args args) {
        if (isSelfConsole(args)) {
            locale.localize(WhitelistMessage.CONSOLE_WHITELISTED).send(args.getSender());
            return;
        }

        final String name;
        final UUID uniqueId;
        if (!args.isEmpty()) {
            name = args.get(0);
            uniqueId = uniqueIdFetcher.getUUID(args.getArgs().get(0));
            if (uniqueId == null) {
                locale.localize(WhitelistMessage.API_REQUEST_FAILED_DIRECT_UUID_NOT_IMPLEMENTED_YET)
                        .set("player", args.get(0))
                        .send(args.getSender());
                return;
            }
        } else {
            name = args.getSender().getName();
            uniqueId = ((Player) args.getSender()).getUniqueId();
        }

        final Optional<Whitelistable> whitelistable = whitelistService.find(uniqueId).join();
        if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
            tellNotWhitelisted(args.getSender(), name);
        } else if (whitelistable.get().isExpirable()) {
            tellWhitelistedTemporarily(args.getSender(), name, whitelistable.get().getExpiresAt());
        } else {
            tellWhitelisted(args.getSender(), name);
        }
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private boolean isSelfConsole(final Args args) {
        return !args.isPlayer() && args.isEmpty();
    }

    private void tellNotWhitelisted(final CommandSender sender, final String name) {
        locale.localize(WhitelistMessage.INFO_NOT_WHITELISTED)
                .set("player", name)
                .send(sender);
    }

    private void tellWhitelistedTemporarily(final CommandSender sender, final String name,
                                            final Date expiresAt) {
        locale.localize(WhitelistMessage.INFO_WHITELISTED_TEMP)
                .set("player", name)
                .set("time", expiresAt.toString())
                .send(sender);
    }

    private void tellWhitelisted(final CommandSender sender, final String name) {
        locale.localize(WhitelistMessage.INFO_WHITELISTED)
                .set("player", name)
                .send(sender);
    }
}
