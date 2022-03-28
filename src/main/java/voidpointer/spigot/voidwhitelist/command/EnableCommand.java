package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

import java.util.Collections;
import java.util.List;

public final class EnableCommand extends Command {
    public static final String NAME = "enable";
    public static final List<String> ALIASES = Collections.singletonList("on");
    public static final String PERMISSION = "whitelist.enable";

    @AutowiredLocale private static Locale locale;
    private final WhitelistConfig whitelistConfig;
    private final EventManager eventManager;

    public EnableCommand(final @NonNull WhitelistConfig whitelistConfig, final @NonNull EventManager eventManager) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.whitelistConfig = whitelistConfig;
        this.eventManager = eventManager;
    }

    @Override public void execute(final Args args) {
        whitelistConfig.enableWhitelist();
        locale.localize(WhitelistMessage.ENABLED).send(args.getSender());
        eventManager.callAsyncEvent(new WhitelistEnabledEvent());
    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localize(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
