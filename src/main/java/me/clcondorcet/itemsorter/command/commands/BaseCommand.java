package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.data.Trusted;
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

public class BaseCommand extends ItemSorterCommand {

    public static BaseCommand BASE_COMMAND = new BaseCommand("base");

    private BaseCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.base");

        if (args.length == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_base_noName);
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
            s.sendMessage(ItemSorter.prefix + msg.cmd_base_nameNotExist.replaceAll("%base%", args[0]));
            return;
        }

        if (!s.hasPermission("itemsorter.command.base.other")) {
            if (s instanceof Player) {
                Player p = (Player) s;
                if (!system.isOwner(p)) {
                    if (!system.canAccess(p) || !s.hasPermission("itemsorter.command.base.trust")) {
                        s.sendMessage(ItemSorter.prefix + msg.cmd_dontHavePermission);
                        return;
                    }
                }
            }
        }
        boolean displayLoc = false;
        if (s instanceof Player) {
            if (!s.hasPermission("itemsorter.command.base.coodinates.other")) {
                if (system.isOwner((Player) s)) {
                    if (s.hasPermission("itemsorter.command.base.coodinates")) {
                        displayLoc = true;
                    }
                } else if (system.canAccess((Player) s)) {
                    if (s.hasPermission("itemsorter.command.base.coodinates.trust")) {
                        displayLoc = true;
                    }
                }
            } else {
                displayLoc = true;
            }
        }
        sendBase(s, msg.cmd_base_header);
        sendBaseNofix(s, msg.cmd_base_name.replaceAll("%name%", system.name));
        sendBasehc(s, msg.cmd_base_owner.replaceAll("%owner%", system.getOwnerName()), msg.cmd_base_ownerhover, "/" + label + " list " + system.getOwnerName());
        FutureLocation loc = system.sign;
        String msgLoc = msg.cmd_base_location.replaceAll("%world%", loc.getWorldName())
                .replaceAll("%x%", loc.getBlockX() + "")
                .replaceAll("%y%", loc.getBlockY() + "")
                .replaceAll("%z%", loc.getBlockZ() + "");
        sendBasePermhs(s, displayLoc, msgLoc, msg.cmd_base_locationHover, loc.getWorldName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());

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
            TextComponent sourceAction = new TextComponent(msg.cmd_base_actions);
            TextComponent glowAction = new TextComponent(msg.cmd_base_actionGlow);
            HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_base_actionGlowHover)});
            ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow base " + system.name + " false");
            glowAction.setHoverEvent(glowActionh);
            glowAction.setClickEvent(glowActionc);
            sourceAction.addExtra(glowAction);
            send(s, sourceAction);
        }
        String trust = msg.cmd_base_trusts;
        List<Trusted> trusted = system.getTrusted();
        if (trusted.size() > 0) {
            trust += msg.cmd_base_trustShem.replaceAll("%trust%", trusted.get(0).name);
            for (int i = 1; i < trusted.size(); i++) {
                trust += msg.cmd_base_trustSeparator + msg.cmd_base_trustShem.replaceAll("%trust%", trusted.get(i).name);
            }
        }
        sendBase(s, trust);
        sendBasePermhs(s, true, msg.cmd_base_filters.replaceAll("%num%", system.getFilters().size() + ""), msg.cmd_base_filtersHover, "/" + label + " filters " + system.name + " ");
        sendBasehc(s, msg.cmd_base_deposits.replaceAll("%num%", system.getDeposits().size() + ""), msg.cmd_base_depositsHover, "/" + label + " deposits " + system.name);
    }
}
