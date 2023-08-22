package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.CachedItems;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class SetOwnerCommand extends ItemSorterCommand {

    public static SetOwnerCommand SET_OWNER_COMMAND = new SetOwnerCommand("setOwner");

    private SetOwnerCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.setowner");

        if(args.length == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_setOwner_noBase);
            return;
        }

        if (args.length == 1) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_setOwner_noPlayer);
        }

        System system = null;
        for (System sys : DataManager.getSystems()) {
            if (sys.name.equals(args[0])) {
                system = sys;
                break;
            }
        }
        if (system == null) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_setOwner_baseNotExist.replaceAll("%base%", args[0]));
            return;
        }

        if(!args[1].equalsIgnoreCase(system.getOwnerName())){
            Player p = Bukkit.getPlayerExact(args[1]);
            try {
                if (p != null && p.isOnline()) {
                    system.setOwner(p);
                } else {
                    system.setOwner(args[1], null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                s.sendMessage(ItemSorter.prefix + msg.msg_error);
                return;
            }
        }
        s.sendMessage(ItemSorter.prefix + msg.cmd_setOwner_complete);
    }
}
