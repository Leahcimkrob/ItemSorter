package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.clcondorcet.itemsorter.command.CommandsMessaging.*;
import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class FiltersCommand extends ItemSorterCommand {

    public static FiltersCommand FILTERS_COMMAND = new FiltersCommand("filters");

    private FiltersCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.filters");

        if (args.length == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_filters_noName);
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
            s.sendMessage(ItemSorter.prefix + msg.cmd_filters_baseNotExist.replaceAll("%base%", args[0]));
            return;
        }

        if (!s.hasPermission("itemsorter.command.filters.other")) {
            if (s instanceof Player) {
                Player p = (Player) s;
                if (!system.canAccess(p)) {
                    s.sendMessage(ItemSorter.prefix + msg.cmd_dontHavePermission);
                    return;
                }
            }
        }
        int page = 0;
        String filter = "";
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                if (args[i].toLowerCase().startsWith("p:")) {
                    try {
                        page = Integer.parseInt(args[i].replaceFirst("p:", ""));
                    } catch (Exception ignored) {
                    }
                } else {
                    filter = args[i];
                }
                if (page != 0 && !filter.equals("")) {
                    break;
                }
            }
        }
        if (!filter.equalsIgnoreCase("Trash")) {
            if (filter.equalsIgnoreCase("item")) {
                if (s instanceof Player) {
                    try {
                        filter = ((Player) s).getInventory().getItemInHand().getType().toString();
                    } catch (Exception ignored) {
                    }
                }
            } else {
                try {
                    filter = Material.valueOf(filter.toUpperCase()).toString();
                } catch (Exception ex) {
                    filter = "Trash";
                }
            }
        } else {
            filter = "Trash";
        }
        ArrayList<Filter> filters = new ArrayList<>();
        for (Filter fil : system.getFilters()) {
            if ((filter.equals("Trash") && fil.isTrash()) || (!filter.equals("Trash") && fil.getMaterials().containsKey(Material.valueOf(filter)))) {
                filters.add(fil);
            }
        }

        if (filters.size() == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_filters_optionEmpty);
            return;
        }

        boolean displayLoc = false;
        if (s instanceof Player) {
            if (!s.hasPermission("itemsorter.command.filters.coodinates.other")) {
                if (system.isOwner((Player) s)) {
                    if (s.hasPermission("itemsorter.command.filters.coodinates")) {
                        displayLoc = true;
                    }
                } else if (system.canAccess((Player) s)) {
                    if (s.hasPermission("itemsorter.command.filters.coodinates.trust")) {
                        displayLoc = true;
                    }
                }
            } else {
                displayLoc = true;
            }
        }
        if (page <= 0) {
            page = 1;
        }
        int max = ((filters.size() - 1) / 10) + 1;
        if (max < page) {
            page = max;
        }
        Filter[] sortedFilters = new Filter[filters.size()];
        sortedFilters = filters.toArray(sortedFilters);
        Material mat = null;
        if (filter.equals("Trash")) {
            Utilities.sort(sortedFilters, (a, b) -> ((Filter) a).getTrashPriority() > ((Filter) b).getTrashPriority());
        } else {
            mat = Material.valueOf(filter);
            Material finalMat = mat;
            Utilities.sort(sortedFilters, (a, b) -> ((Filter) a).getMaterials().get(finalMat).getPriority() > ((Filter) b).getMaterials().get(finalMat).getPriority());
        }
        sendBase(s, msg.cmd_filters_header);
        if (filter.equals("Trash")) {
            sendBase(s, msg.cmd_filters_option.replaceAll("%option%", filter));
        } else {
            sendBasehc(s, msg.cmd_filters_option.replaceAll("%option%", filter), msg.cmd_filters_hoverOption, "/" + label + " filters " + system.name + " false");
        }
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
            TextComponent sourceAction = new TextComponent(msg.cmd_filters_actions);
            TextComponent glowAction = new TextComponent(msg.cmd_filters_actionGlow);
            HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_filters_actionGlowAllHover)});
            ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow filters " + system.name + " " + filter + " false");
            glowAction.setHoverEvent(glowActionh);
            glowAction.setClickEvent(glowActionc);
            sourceAction.addExtra(glowAction);
            send(s, sourceAction);
        }
        for (int i = (page - 1) * 10; i < sortedFilters.length && i < ((page - 1) * 10) + 10; i++) {
            TextComponent sourceAction = new TextComponent(msg.cmd_filters_prefix + msg.cmd_filters_priority.replaceAll("%num%", (sortedFilters[i].isTrash() ? sortedFilters[i].getTrashPriority() : sortedFilters[i].getMaterials().get(mat).getPriority()) + ""));
            if (displayLoc) {
                HoverEvent hoverloc = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_filters_locationHover)});
                FutureLocation loc = sortedFilters[i].sign;
                ClickEvent clickloc = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                TextComponent location = new TextComponent(msg.cmd_filters_location.replaceAll("%x%", loc.getBlockX() + "").replaceAll("%y%", loc.getBlockY() + "").replaceAll("%z%", loc.getBlockZ() + ""));
                location.setHoverEvent(hoverloc);
                location.setClickEvent(clickloc);
                sourceAction.addExtra(location);
            }
            if (ItemSorter.versionHandler.isGlowAvailable() && canGlow) {
                TextComponent glowAction = new TextComponent(msg.cmd_filters_actionGlow);
                HoverEvent glowActionh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_filters_actionGlowHover)});
                ClickEvent glowActionc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " glow filters " + system.name + " " + filter + " " + (sortedFilters[i].isTrash() ? sortedFilters[i].getTrashPriority() : sortedFilters[i].getMaterials().get(mat).getPriority()) + " false");
                glowAction.setHoverEvent(glowActionh);
                glowAction.setClickEvent(glowActionc);
                sourceAction.addExtra(glowAction);
            }
            send(s, sourceAction);
        }
        sendNav(s, page, 1, max, "/" + label + " filters " + system.name + " " + filter + " p:%num%");
    }
}
