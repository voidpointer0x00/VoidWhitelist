package voidpointer.spigot.voidwhitelist.command;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.voidwhitelist.config.WhitelistConfig;
import voidpointer.spigot.voidwhitelist.event.WhitelistDisabledEvent;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;

import java.util.Arrays;
import java.util.List;

public final class DisableCommand extends Command {
    public static final String NAME = "disable";
    public static final List<String> ALIASES = Arrays.asList("off");
    public static final String PERMISSION = "whitelist.disable";

    @NonNull private final WhitelistConfig whitelistConfig;
    @NonNull private final Locale locale;

    public DisableCommand(@NonNull final WhitelistConfig whitelistConfig, @NonNull final Locale locale) {
        super(NAME);
        super.setPermission(PERMISSION);

        this.whitelistConfig = whitelistConfig;
        this.locale = locale;
    }

    @Override public void execute(final Args args) {
        whitelistConfig.disableWhitelist();
        locale.localizeColorized(WhitelistMessage.DISABLED).send(args.getSender());

        Bukkit.getServer().getPluginManager().callEvent(new WhitelistDisabledEvent());
    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }

    @Override protected void onNoPermission(final CommandSender sender) {
        locale.localizeColorized(WhitelistMessage.NO_PERMISSION).send(sender);
    }
}
