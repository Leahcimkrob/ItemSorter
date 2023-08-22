package me.clcondorcet.itemsorter.command;

import com.sun.istack.internal.NotNull;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

abstract class CommandDispatcher extends ItemSorterCommand {

    public ArrayList<ItemSorterCommand> children;
    public ItemSorterCommand defaultCommand;

    public CommandDispatcher(String name, List<ItemSorterCommand> children, @NotNull ItemSorterCommand defaultCommand, String... aliases) {
        super(name, aliases);
        this.children = new ArrayList<>();
        if (children != null) {
            this.children.addAll(children);
        }
        this.defaultCommand = defaultCommand;
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        String command = "";
        String[] newArgs;
        if (args.length > 0) {
            command = args[0];
            newArgs = new String[args.length - 1];
            int i = 0;
            for (String arg : args) {
                if (i != 0) {
                    newArgs[(i++) - 1] = arg;
                } else {
                    i = 1;
                }
            }
        } else {
            newArgs = new String[0];
        }

        for (ItemSorterCommand cmd : children) {
            boolean runCmd = cmd.name.equalsIgnoreCase(command);
            if (!runCmd) {
                for (String alias : cmd.aliases) {
                    if (alias.equalsIgnoreCase(command)) {
                        runCmd = true;
                        break;
                    }
                }
            }
            if (runCmd) {
                cmd.runCommand(s, label, newArgs);
                return;
            }
        }

        defaultCommand.runCommand(s, label, newArgs);
    }
}
