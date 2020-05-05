/*
 * Copyright (c) 2020 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
package voidpointer.bukkit.whitelist.subcommand;

import static java.lang.String.valueOf;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import voidpointer.bukkit.framework.command.CommandArgs;
import voidpointer.bukkit.framework.command.SimpleCommand;
import voidpointer.bukkit.framework.command.validator.PermissionValidator;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.whitelist.config.WhitelistConfig;
import voidpointer.bukkit.whitelist.message.ErrorMessage;
import voidpointer.bukkit.whitelist.message.InfoMessage;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistReloadSubCommand extends SimpleCommand {
    private static final String RELOAD_COMMAND_NAME = "reload";
    private static final String RELOAD_COMMAND_DISPLAY_NAME = "whitelist reload";
    private static final String RELOAD_COMMAND_PERMISSION = "voidwhitelist.reload";

    @NonNull private final Locale locale;
    @NonNull private final WhitelistConfig pluginConfig;

    public WhitelistReloadSubCommand(final Locale locale, final WhitelistConfig pluginConfig) {
        super(RELOAD_COMMAND_NAME);
        super.addValidator(new PermissionValidator(locale));
        this.locale = locale;
        this.pluginConfig = pluginConfig;
    }

    @Override public boolean execute(final CommandArgs args) {
        final long startNanos = System.nanoTime();
        pluginConfig.reload().thenAcceptAsync(isReloaded -> {
            if (isReloaded.booleanValue())
                onReloaded(startNanos, args);
            else
                onReloadFailure(args);
        });
        return true;
    }

    private void onReloaded(final long startNanos, final CommandArgs args) {
        final long endNanos = System.nanoTime();
        final long totalReloadNanos = endNanos - startNanos;
        locale.getLocalized(InfoMessage.RELOADED)
                .colorize()
                .set("ns", valueOf(totalReloadNanos))
                .set("ms", valueOf(TimeUnit.NANOSECONDS.toMillis(totalReloadNanos)))
                .send(args.getSender());
    }

    private void onReloadFailure(final CommandArgs args) {
        locale.getLocalized(ErrorMessage.UNABLE_TO_RELOAD)
                .colorize()
                .send(args.getSender());
    }

    @Override public String getDisplayName() {
        return RELOAD_COMMAND_DISPLAY_NAME;
    }

    @Override public String getPermission() {
        return RELOAD_COMMAND_PERMISSION;
    }

    @Override public List<String> complete(final CommandArgs args) {
        return Arrays.asList(""); /* nothing to complete */
    }
}
