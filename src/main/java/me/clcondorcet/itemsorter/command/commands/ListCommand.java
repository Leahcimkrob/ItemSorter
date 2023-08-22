package me.clcondorcet.itemsorter.command.commands;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.ItemSorterCommand;
import me.clcondorcet.itemsorter.data.CachedItems;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

import static me.clcondorcet.itemsorter.command.CommandsMessaging.*;
import static me.clcondorcet.itemsorter.command.MainCommand.msg;

public class ListCommand extends ItemSorterCommand {

    public static ListCommand LIST_COMMAND = new ListCommand("list");

    private ListCommand(String name) {
        super(name);
    }

    @Override
    public void runCommand(CommandSender s, String label, String[] args) throws CommandReturn {
        checkPerm(s, "itemsorter.command.list");

        int page = 0;
        String filter = "";

        if (args.length > 0) {
            for (String arg : args) {
                if (arg.toLowerCase().startsWith("p:")) {
                    try {
                        page = Integer.parseInt(arg.replaceFirst("p:", ""));
                    } catch (Exception ignored) {}
                } else {
                    filter = arg;
                }
                if (page != 0 && !filter.equals("")) {
                    break;
                }
            }
        }

        ArrayList<System> systems = new ArrayList<>();
        if (!filter.equals("")) {
            for (System sys : DataManager.getSystems()) {
                if (sys.getOwnerName().equalsIgnoreCase(filter)) {
                    systems.add(sys);
                }
            }
        } else {
            systems.addAll(DataManager.getSystems());
        }
        if (systems.size() == 0) {
            s.sendMessage(ItemSorter.prefix + msg.cmd_list_listEmpty);
        }

        if (page <= 0) {
            page = 1;
        }
        int max = ((systems.size() - 1) / 10) + 1;
        if (max < page) {
            page = max;
        }
        sendBase(s, msg.cmd_list_header);
        if (!filter.equals("")) {
            TextComponent optionChat = new TextComponent(msg.cmd_list_option.replaceAll("%option%", filter));
            HoverEvent optionhover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_list_hoverOption)});
            ClickEvent optionclick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list");
            optionChat.setClickEvent(optionclick);
            optionChat.setHoverEvent(optionhover);
            send(s, optionChat);
        }
        for (int i = (page-1) * 10; i < systems.size() && i < ((page-1) * 10) + 10; i++) {
            TextComponent sourceChat = new TextComponent(msg.cmd_list_prefix);
            TextComponent baseChat = new TextComponent(msg.cmd_list_name.replaceAll("%name%", systems.get(i).name));
            HoverEvent basehover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_list_hoverName)});
            ClickEvent baseclick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " base " + systems.get(i).name);
            baseChat.setClickEvent(baseclick);
            baseChat.setHoverEvent(basehover);
            TextComponent ownerChat = new TextComponent(msg.cmd_list_owner.replaceAll("%name%", systems.get(i).getOwnerName()));
            HoverEvent ownerhover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(msg.cmd_list_hoverOwner)});
            ClickEvent ownerclick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " list " + systems.get(i).getOwnerName());
            ownerChat.setHoverEvent(ownerhover);
            ownerChat.setClickEvent(ownerclick);
            sourceChat.addExtra(baseChat);
            sourceChat.addExtra(ownerChat);
            send(s, sourceChat);
        }
        sendNav(s, page, 1, max, "/" + label + " list " + filter + " p:%num%");
    }
}
