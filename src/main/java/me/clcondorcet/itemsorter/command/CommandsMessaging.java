package me.clcondorcet.itemsorter.command;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.config.Messages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsMessaging {

    public static void sendBase(CommandSender s, String st){
        if(!st.equals("")){
            send(s, new TextComponent(Messages.replaceColorCode(st)));
        }
    }

    public static void sendBaseNofix(CommandSender s, String st){
        if(!st.equals("")){
            send(s, new TextComponent(Messages.replaceColorCode(st)));
        }
    }

    public static void sendBasePermhs(CommandSender s, boolean condition, String st, String hover, String suggest) {
        try {
            if (!st.equals("") && condition) {
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Messages.replaceColorCode(hover))});
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest);
                TextComponent chatComponent = new TextComponent(Messages.replaceColorCode(st));
                chatComponent.setHoverEvent(hoverEvent);
                chatComponent.setClickEvent(clickEvent);
                send(s, chatComponent);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void sendBasehc(CommandSender s, String st, String hover, String cmd) {
        try {
            if (!st.equals("")) {
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Messages.replaceColorCode(hover))});
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd);
                TextComponent chatComponent = new TextComponent(Messages.replaceColorCode(st));
                chatComponent.setHoverEvent(hoverEvent);
                chatComponent.setClickEvent(clickEvent);
                send(s, chatComponent);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void sendNav(CommandSender s, int page, int min, int max, String toRun){
        Messages msg = ItemSorter.configManager.messages;
        TextComponent source = new TextComponent(msg.cmd_nav_before);
        if(page > min){
            TextComponent prev = new TextComponent(msg.cmd_nav_previous);
            HoverEvent prevh = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent((page - 1) + "")});
            ClickEvent prevc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, toRun.replaceAll("%num%", (page - 1) + ""));
            prev.setHoverEvent(prevh);
            prev.setClickEvent(prevc);
            source.addExtra(prev);
        }
        TextComponent count = new TextComponent(msg.cmd_nav_pageCount.replaceAll("%page%", page + "").replaceAll("%max%", max + ""));
        source.addExtra(count);
        if(page < max){
            TextComponent next = new TextComponent(msg.cmd_nav_next);
            HoverEvent nexth = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent((page + 1) + "")});
            ClickEvent nextc = new ClickEvent(ClickEvent.Action.RUN_COMMAND, toRun.replaceAll("%num%", (page + 1) + ""));
            next.setHoverEvent(nexth);
            next.setClickEvent(nextc);
            source.addExtra(next);
        }
        source.addExtra(new TextComponent(msg.cmd_nav_after));
        send(s, source);
    }

    public static void send(CommandSender s, BaseComponent comp){
        if(s instanceof Player){
            ((Player)s).spigot().sendMessage(comp);
        }else{
            s.sendMessage(comp.toLegacyText());
        }
    }

}
