package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static me.clcondorcet.itemsorter.command.CommandsMessaging.*;
import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class DepositsCommand extends ItemSorterCommand {

    public static DepositsCommand DEPOSITS_COMMAND = new DepositsCommand("deposits");

    private DepositsCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.deposits");

        if (args.length == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_deposits_noName);
            return;
        }

        System system = null;
        for (System sys : DataManager.getSystems()) {
            if (sys.name.equals(args[0])) {
                system = sys;
                break;
            }
        }

        if (system == null) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_deposits_baseNotExist.replaceAll("%base%", args[0]));
            return;
        }

        if (!s.hasPermission("itemsorter.command.deposits.other")) {
            if (s instanceof Player) {
                Player p = (Player) s;
                if (!system.canAccess(p)) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_dontHavePermission);
                    return;
                }
            }
        }

        int page = 0;
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                if (args[i].toLowerCase().startsWith("p:")) {
                    try {
                        page = Integer.parseInt(args[i].replaceFirst("p:", ""));
                    } catch (Exception ignored) {
                    }
                }
                if (page != 0) {
                    break;
                }
            }
        }
        List<Deposit> deposits = system.getDeposits();

        if (deposits.size() == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_deposits_empty);
            return;
        }

        if (page <= 0) {
            page = 1;
        }
        int max = ((deposits.size() - 1) / 10) + 1;
        if (max < page) {
            page = max;
        }
        sendBase(s, msg.cmd_deposits_header);
        boolean canGlow = false;
        if (s instanceof Player) {
            if (!s.hasPermission("itemsorter.command.glow.other")) {
                if (system.isOwner((Player) s)) {
                    if (s.hasPermission("itemsorter.command.glow")) {
                        canGlow = true;
                    }
                } else if (system.canAccess((Player) s)) {
                    if (s.hasPermission("itemsorter.command.glow.trust")) {
                        canGlow = true;
                    }
                }
            } else {
                canGlow = true;
            }
        }
        if (ItemSorter.versionHandler.isGlowAvailable() && canGlow) {
            TextComponent sourceAction = new TextComponent(msg.cmd_deposits_actions);
            TextComponent glowAction = new TextComponent(msg.cmd_deposits_actionGlow);
            HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_deposits_actionGlowAllHover)});
            ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow deposits " + system.name + " false");
            glowAction.setHoverEvent(glowActionh);
            glowAction.setClickEvent(glowActionc);
            sourceAction.addExtra(glowAction);
            send(s, sourceAction);
        }
        for (int i = (page - 1) * 10; i < deposits.size() && i < ((page - 1) * 10) + 10; i++) {
            TextComponent sourceAction = new TextComponent(msg.cmd_deposits_prefix);
            HoverEvent hoverloc = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_deposits_locationHover)});
            FutureLocation loc = deposits.get(i).sign;
            ClickEvent clickloc = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            TextComponent location = new TextComponent(msg.cmd_deposits_location.replaceAll("%x%", loc.getBlockX() + "").replaceAll("%y%", loc.getBlockY() + "").replaceAll("%z%", loc.getBlockZ() + ""));
            location.setHoverEvent(hoverloc);
            location.setClickEvent(clickloc);
            sourceAction.addExtra(location);
            if (ItemSorter.versionHandler.isGlowAvailable() && canGlow) {
                TextComponent glowAction = new TextComponent(msg.cmd_deposits_actionGlow);
                HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_deposits_actionGlowHover)});
                ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow deposits " + system.name + " " + i + " false");
                glowAction.setHoverEvent(glowActionh);
                glowAction.setClickEvent(glowActionc);
                sourceAction.addExtra(glowAction);
            }
            send(s, sourceAction);
        }
        sendNav(s, page, 1, max, "/" + label + " deposits " + system.name + " p:%num%");
    }
}
