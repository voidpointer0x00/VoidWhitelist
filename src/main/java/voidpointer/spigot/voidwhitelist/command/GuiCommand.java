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
package voidpointer.spigot.voidwhitelist.command;

import voidpointer.spigot.framework.di.Autowired;
import voidpointer.spigot.voidwhitelist.gui.WhitelistGui;
import voidpointer.spigot.voidwhitelist.storage.WhitelistService;

public final class GuiCommand extends Command {
    private static final String NAME = "gui";
    private static final String PERMISSION = "whitelist.gui";

    @Autowired static WhitelistService whitelistService;

    public GuiCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {
        if (!args.isPlayer()) {
            args.getSender().sendMessage("I hate console, you can't use this command! >:C");
            return;
        }
        WhitelistGui gui = new WhitelistGui();
        gui.show(args.getPlayer());
        whitelistService.findAll(gui.availableProfileSlots()).thenAcceptAsync(gui::fillCurrentPage);
    }
}
