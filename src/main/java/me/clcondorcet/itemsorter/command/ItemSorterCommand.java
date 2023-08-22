package me.clcondorcet.itemsorter.command;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public abstract class ItemSorterCommand {

    public String name;
    public ArrayList<String> aliases;

    public ItemSorterCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = new ArrayList<>(Arrays.asList(aliases));
    }

    public abstract void runCommand(CommandSender s, String label, String[] args) throws CommandReturn;

    public void checkPerm(CommandSender s, String permission) throws CommandReturn {
        checkPerm(s, permission, msg.cmd_dontHavePermission);
    }

    public void checkPerm(CommandSender s, String permission, String messageNoPerm) throws CommandReturn {
        if (!s.hasPermission(permission)) {
            s.sendMessage(ItemSorter.prefix + messageNoPerm);
            throw RETURN;
        }
    }

    protected static class CommandReturn extends Exception {}
    public static final CommandReturn RETURN = new CommandReturn();
}
