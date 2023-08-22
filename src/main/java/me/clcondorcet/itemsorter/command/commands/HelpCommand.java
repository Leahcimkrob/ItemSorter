package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import org.bukkit.command.CommandSender;

import static me.clcondorcet.itemsorter.command.CommandsMessaging.sendBase;
import static me.clcondorcet.itemsorter.command.CommandsMessaging.sendBasePermhs;
import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class HelpCommand extends ItemSorterCommand {

    public static HelpCommand HELP_COMMAND = new HelpCommand("help");

    private HelpCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.help");

        sendBase(s, msg.cmd_help_header);
        sendBase(s, msg.cmd_help_space1);
        sendBasePermhs(s, s.hasPermission("itemsorter.command.help"), msg.cmd_help_help.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " help");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.list"), msg.cmd_help_list.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " list ");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.base"), msg.cmd_help_base.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " base ");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.filters"), msg.cmd_help_filters.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " filters ");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.deposits"), msg.cmd_help_deposits.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " deposits ");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.glow") && ItemSorter.versionHandler.isGlowAvailable(), msg.cmd_help_glow.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " glow ");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.autosign"), msg.cmd_help_autosign.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " autosign ");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.reload"), msg.cmd_help_reload.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " reload");
        sendBasePermhs(s, s.hasPermission("itemsorter.command.setowner"), msg.cmd_help_setOwner.replaceAll("%cmd%", label), msg.cmd_help_hover, "/" + label + " setOwner ");
        sendBase(s, msg.cmd_help_space2);
        sendBase(s, msg.cmd_help_footer);
    }
}
