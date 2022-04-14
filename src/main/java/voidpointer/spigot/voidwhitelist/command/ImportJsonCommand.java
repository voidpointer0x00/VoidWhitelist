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

import java.util.Collections;
import java.util.List;

public class ImportJsonCommand extends Command {
    public static final String NAME = "import-json";
    public static final String PERMISSION = "permission";
    public static final List<String> ALIASES = Collections.singletonList("importjson");

    public ImportJsonCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {

    }

    @Override public List<String> getAliases() {
        return ALIASES;
    }
}
