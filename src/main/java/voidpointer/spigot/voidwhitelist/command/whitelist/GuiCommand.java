/*
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *
 *  Copyright (C) 2022 Vasiliy Petukhov <void.pointer@ya.ru>
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 *
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *   0. You just DO WHAT THE FUCK YOU WANT TO.
 */
package voidpointer.spigot.voidwhitelist.command.whitelist;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.framework.localemodule.Locale;
import voidpointer.spigot.framework.localemodule.annotation.AutowiredLocale;
import voidpointer.spigot.voidwhitelist.command.Command;
import voidpointer.spigot.voidwhitelist.command.arg.Args;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.message.WhitelistMessage;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;
import voidpointer.spigot.voidwhitelist.version.Version;

public final class GuiCommand extends Command {
    private static final String NAME = "gui";
    private static final String PERMISSION = "whitelist.gui";

    @AutowiredLocale private static Locale locale;
    @Autowired(mapId="whitelistService")
    private static WhitelistService whitelistService;

    public GuiCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        if (!args.isPlayer()) {
            args.getSender().sendMessage("I hate console, you can't use this command! >:C");
            return;
        }
        if (!Version.supportsGui()) {
            locale.localize(WhitelistMessage.GUI_NOT_SUPPORTED)
                    .set("major", WhitelistGui.MAJOR_VERSION_REQUIRED)
                    .send(args.getSender());
            return;
        }
        WhitelistGui gui = new WhitelistGui();
        gui.show(args.getPlayer());
        whitelistService.findAll(gui.availableProfileSlots()).thenAcceptAsync(gui::fillCurrentPage);
    }
}
