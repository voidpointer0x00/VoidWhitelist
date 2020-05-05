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

import java.util.Arrays;
import java.util.List;

import lombok.NonNull;
import voidpointer.bukkit.framework.command.CommandArgs;
import voidpointer.bukkit.framework.command.SimpleCommand;
import voidpointer.bukkit.framework.command.validator.PermissionValidator;
import voidpointer.bukkit.framework.locale.Locale;
import voidpointer.bukkit.whitelist.config.WhitelistConfig;
import voidpointer.bukkit.whitelist.message.ErrorMessage;
import voidpointer.bukkit.whitelist.message.InfoMessage;

/** @author VoidPointer aka NyanGuyMF */
public final class WhitelistDisableSubCommand extends SimpleCommand {
    private static final String DISABLE_COMMAND_NAME = "disable";
    private static final String DISABLE_COMMAND_DISPLAY_NAME = "whitelist disable";
    private static final String DISABLE_COMMAND_PERMISSION = "voidwhitelist.disable";

    @NonNull private final Locale locale;
    @NonNull private final WhitelistConfig whitelistConfig;

    public WhitelistDisableSubCommand(final Locale locale, final WhitelistConfig whitelistConfig) {
        super(DISABLE_COMMAND_NAME);
        super.addValidator(new PermissionValidator(locale));
        this.whitelistConfig = whitelistConfig;
        this.locale = locale;
    }

    @Override public boolean execute(final CommandArgs args) {
        whitelistConfig.setEnabled(false);
        whitelistConfig.save().thenAcceptAsync(isSaved -> {
            if (isSaved.booleanValue())
                onSaved(args);
            else
                onSaveFailure(args);
        });
        return true;
    }

    private void onSaved(final CommandArgs args) {
        locale.getLocalized(InfoMessage.ENABLED_AND_SAVED)
                .colorize()
                .send(args.getSender());
    }

    private void onSaveFailure(final CommandArgs args) {
        locale.getLocalized(ErrorMessage.ENABLED_BUT_NOT_SAVED)
                .colorize()
                .multiline()
                .send(args.getSender());
    }

    @Override public List<String> complete(final CommandArgs args) {
        return Arrays.asList(""); /* nothing to complete */
    }

    @Override public String getDisplayName() {
        return DISABLE_COMMAND_DISPLAY_NAME;
    }

    @Override public String getPermission() {
        return DISABLE_COMMAND_PERMISSION;
    }
}
