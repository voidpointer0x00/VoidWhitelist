package voidpointer.spigot.voidwhitelist.command;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistReconnectEvent;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

import static voidpointer.spigot.voidwhitelist.message.WhitelistMessage.*;
import static voidpointer.spigot.voidwhitelist.storage.WhitelistService.ReconnectResult;

public final class ReconnectCommand extends Command {
    public static final String NAME = "reconnect";
    public static final String PERMISSION = "whitelist.reconnect";

    @AutowiredLocale private static Locale locale;
    @Autowired private static WhitelistService whitelistService;
    @Autowired private static EventManager eventManager;

    public ReconnectCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        whitelistService.shutdown();
        ReconnectResult reconnectResult = whitelistService.reconnect();
        if (reconnectResult.isSuccess())
            locale.localize(RECONNECT_SUCCESS).send(args.getSender());
        else
            locale.localize(RECONNECT_FAIL).send(args.getSender());
        eventManager.callAsyncEvent(new WhitelistReconnectEvent(reconnectResult, args.getSender()));
    }
}