package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public final class InfoCommand extends Command {
    public static final String NAME = "info";
    public static final String PERMISSION = "whitelist.info";

    @NonNull private final Locale locale;
    @NonNull private final WhitelistService whitelistService;

    public InfoCommand(WhitelistService whitelistService, Locale locale) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.locale = locale;
        this.whitelistService = whitelistService;
    }

    @Override public void execute(final Args args) {
        if (isSelfConsole(args)) {
            locale.localizeColorized(WhitelistMessage.CONSOLE_WHITELISTED).send(args.getSender());
            return;
        }

        final String name;
        final UUID uniqueId;
        if (args.isEmpty()) {
            name = args.getSender().getName();
            uniqueId = ((Player) args.getSender()).getUniqueId();
        } else {
            name = args.get(0);
            try {
                uniqueId = UUIDFetcher.getUUID(name);
            } catch (IOException ioException) {
                /* TODO: implement direct UUID info command */
                /* TODO: implement IllegalArgumentException handling */
                locale.localizeColorized(WhitelistMessage.API_REQUEST_FAILED_DIRECT_UUID_NOT_IMPLEMENTED_YET)
                        .set("player", name)
                        .send(args.getSender());
                ioException.printStackTrace();
                return;
            }
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
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private boolean isSelfConsole(final Args args) {
        return !args.isPlayer() && args.isEmpty();
    }

    private void tellNotWhitelisted(final CommandSender sender, final String name) {
        locale.localizeColorized(WhitelistMessage.INFO_NOT_WHITELISTED)
                .set("player", name)
                .send(sender);
    }

    private void tellWhitelistedTemporarily(final CommandSender sender, final String name,
                                            final Date expiresAt) {
        locale.localizeColorized(WhitelistMessage.INFO_WHITELISTED_TEMP)
                .set("player", name)
                .set("time", expiresAt.toString())
                .send(sender);
    }

    private void tellWhitelisted(final CommandSender sender, final String name) {
        locale.localizeColorized(WhitelistMessage.INFO_WHITELISTED)
                .set("player", name)
                .send(sender);
    }
}
