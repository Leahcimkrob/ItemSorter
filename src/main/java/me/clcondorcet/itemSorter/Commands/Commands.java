package me.clcondorcet.itemSorter.Commands;

import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Objects.CachedItems;
import me.clcondorcet.itemSorter.Objects.System;
import me.clcondorcet.itemSorter.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(s.hasPermission("is.admin")){
			Main.configManager.loadConfigs();
			Main.loadConfigs(s);
			Main.cachedItems = new CachedItems();
			s.sendMessage(Main.prefix + Main.configManager.messages.msg_reload.replaceAll("&", "§"));
		}else{
			s.sendMessage(Main.prefix + Main.configManager.messages.msg_dontHavePermission.replaceAll("&", "§"));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args){
		//  args.lenth == 2
		if(args.length == 2 && args[0].equalsIgnoreCase("bases")){
			ArrayList<String> owners = new ArrayList<>();
			for(System sys : Main.bases){
				owners.add(sys.owner);
			}
			return Utilities.searchforsimilarity(args[1], owners);
		}

		//  args.lenth == 1
		if(args.length == 1){
			List<String> list = Arrays.asList("reload", "bases");
			return Utilities.searchforsimilarity(args[0], list);
		}else{
			return null;
		}
	}
}
