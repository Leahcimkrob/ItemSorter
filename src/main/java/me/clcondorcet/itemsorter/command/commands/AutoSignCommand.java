package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.clcondorcet.itemsorter.command.MainCommand.msg;
import static me.clcondorcet.itemsorter.events.EventsManager.autodeposits;
import static me.clcondorcet.itemsorter.events.EventsManager.autofilters;

public class AutoSignCommand extends ItemSorterCommand {

    public static AutoSignCommand AUTO_SIGN_COMMAND = new AutoSignCommand("autosign");

    private AutoSignCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.autosign");

        if (!(s instanceof Player)) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_notPlayer);
            return;
        }

        if (args.length == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_noOption);
            return;
        }

        if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("filter")) {
            if (args.length > 1) {
                System system = null;
                for (System sys : DataManager.getSystems()) {
                    if (sys.name.equals(args[1])) {
                        system = sys;
                        break;
                    }
                }
                if (system != null) {
                    if (!system.canAccess((Player) s)) {
                        s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_notTrust);
                        return;
                    }
                    if (args[0].equalsIgnoreCase("deposit")) {
                        autofilters.remove((Player) s);
                        autodeposits.put((Player) s, system);
                    } else {
                        autodeposits.remove((Player) s);
                        autofilters.put((Player) s, system);
                    }
                    s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_complete.replaceAll("%cmd%", label));
                } else {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_baseNotExist.replaceAll("%base%", args[1]));
                }
            } else {
                s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_noBase);
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            autofilters.remove((Player) s);
            autodeposits.remove((Player) s);
            s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_stop);
        } else {
            s.sendMessage(ItemSorter.prefix + msg.cmd_autosign_noOption);
        }
    }
}
