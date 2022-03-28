package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class InfoCommand extends Command {
    public static final String NAME = "info";
    public static final String PERMISSION = "whitelist.info";

    @AutowiredLocale private static Locale locale;
    @NonNull private final WhitelistService whitelistService;
    @NonNull private final UUIDFetcher uniqueIdFetcher;

    public InfoCommand(final WhitelistService whitelistService, final UUIDFetcher uniqueIdFetcher) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.whitelistService = whitelistService;
        this.uniqueIdFetcher = uniqueIdFetcher;
    }

    @Override public void execute(final Args args) {
        if (isSelfConsole(args)) {
            locale.localize(WhitelistMessage.CONSOLE_WHITELISTED).send(args.getSender());
            return;
        }

        getUniqueId(args).thenAcceptAsync(uuidOptional -> {
            final String name = args.get(0);
            if (!uuidOptional.isPresent()) {
                locale.localize(WhitelistMessage.API_REQUEST_FAILED_DIRECT_UUID_NOT_IMPLEMENTED_YET)
                        .set("player", name)
                        .send(args.getSender());
            }
            final Optional<Whitelistable> whitelistable = whitelistService.find(uuidOptional.get()).join();
            if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
                tellNotWhitelisted(args.getSender(), name);
            } else if (whitelistable.get().isExpirable()) {
                tellWhitelistedTemporarily(args.getSender(), name, whitelistable.get().getExpiresAt());
            } else {
                tellWhitelisted(args.getSender(), name);
            }
        });
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }

    private CompletableFuture<Optional<UUID>> getUniqueId(final Args args) {
        if (args.isEmpty()) {
            return uniqueIdFetcher.getUUID(args.get(0));
        } else {
            return CompletableFuture.completedFuture(Optional.of(args.getPlayer().getUniqueId()));
        }
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
