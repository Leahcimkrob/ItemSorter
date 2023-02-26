package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.command.MainCommand;
import me.clcondorcet.itemsorter.data.CachedItems;
import me.clcondorcet.itemsorter.data.DataManager;
import org.bukkit.command.CommandSender;

import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class ReloadCommand extends ItemSorterCommand {

    public static ReloadCommand RELOAD_COMMAND = new ReloadCommand("reload");

    private ReloadCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.reload");

        ItemSorter.configManager.loadConfigs();
        DataManager.cachedItems = new CachedItems();
        msg = ItemSorter.configManager.messages;
        s.sendMessage(ItemSorter.prefix + msg.cmd_reload_complete);
    }
}
