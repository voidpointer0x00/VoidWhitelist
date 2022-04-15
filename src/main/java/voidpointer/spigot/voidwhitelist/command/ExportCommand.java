package voidpointer.spigot.voidwhitelist.command;

import voidpointer.spigot.voidwhitelist.command.arg.Args;

public class ExportCommand extends Command {
    public static final String NAME = "export-db";
    public static final String PERMISSION = "whitelist.export";

    public ExportCommand() {
        super(NAME);
        super.setPermission(PERMISSION);
    }

    @Override public void execute(final Args args) {

    }
}
