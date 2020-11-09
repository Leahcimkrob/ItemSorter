package me.clcondorcet.itemSorter.Commands;

import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Objects.CachedItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String labl, String[] args) {
		if(s.hasPermission("is.admin")){
			Main.configManager.loadConfigs();
			Main.loadConfigs(s);
			Main.cachedItems = new CachedItems();
			s.sendMessage(Main.prefix + Main.configManager.config.msg_reload.replaceAll("&", "§"));
		}else{
			s.sendMessage(Main.prefix + Main.configManager.config.msg_dontHavePermission.replaceAll("&", "§"));
		}
		return true;
	}
}
