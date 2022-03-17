package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.Whitelistable;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistRemovedEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.uuid.UUIDFetcher;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

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
        final String name = args.get(0);
        final UUID uniqueId;
        try {
            uniqueId = UUIDFetcher.getUUID(name);
        } catch (IOException ioException) {
            /* TODO: implement direct uuid remove command */
            /* TODO: implement IllegalArgumentException handling */
            locale.localizeColorized(WhitelistMessage.API_REQUEST_FAILED_DIRECT_UUID_NOT_IMPLEMENTED_YET)
                    .set("player", name)
                    .send(args.getSender());
            ioException.printStackTrace();
            return;
        }

        final Optional<Whitelistable> whitelistable = whitelistService.find(uniqueId).join();
        if (!whitelistable.isPresent() || !whitelistable.get().isAllowedToJoin()) {
            locale.localizeColorized(WhitelistMessage.REMOVE_NOT_WHITELISTED)
                    .set("player", name)
                    .send(args.getSender());
        } else {
            whitelistService.remove(whitelistable.get());
            locale.localizeColorized(WhitelistMessage.REMOVED)
                    .set("player", name)
                    .send(args.getSender());
        }
        eventManager.callEvent(new WhitelistRemovedEvent(whitelistable.get()));
    }

    @Override protected void onNotEnoughArgs(final CommandSender sender, final Args args) {
        locale.localizeColorized(WhitelistMessage.REMOVE_HELP).send(sender);
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
