package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.EventManager;
import voidpointer.spigot.voidwhitelist.event.WhitelistEnabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

import java.util.Arrays;
import java.util.List;

public final class EnableCommand extends Command {
    public static final String NAME = "enable";
    public static final List<String> ALIASES = Arrays.asList("on");
    public static final String PERMISSION = "whitelist.enable";

    @NonNull private final WhitelistConfig whitelistConfig;
    @NonNull private final Locale locale;
    @NonNull private final EventManager eventManager;

    public EnableCommand(@NonNull final WhitelistConfig whitelistConfig, @NonNull final Locale locale,
                         final EventManager eventManager) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.whitelistConfig = whitelistConfig;
        this.locale = locale;
        this.eventManager = eventManager;
    }

    @Override public void execute(final Args args) {
        whitelistConfig.enableWhitelist();
        locale.localizeColorized(WhitelistMessage.ENABLED).send(args.getSender());
        eventManager.callAsyncEvent(new WhitelistEnabledEvent());
    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
