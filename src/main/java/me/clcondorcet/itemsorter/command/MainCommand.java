package me.clcondorcet.itemsorter.command;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.command.commands.*;
import me.clcondorcet.itemsorter.data.*;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.Utilities;
import me.clcondorcet.itemsorter.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author clcondorcet
 */
public class MainCommand extends CommandDispatcher implements CommandExecutor, TabCompleter {

	public static Messages msg = ItemSorter.configManager.messages;

	public static final MainCommand MAIN_COMMAND = new MainCommand("ItemSorter", Arrays.asList(
			HelpCommand.HELP_COMMAND,
			ReloadCommand.RELOAD_COMMAND,
			ListCommand.LIST_COMMAND,
			BaseCommand.BASE_COMMAND,
			FiltersCommand.FILTERS_COMMAND,
			DepositsCommand.DEPOSITS_COMMAND,
			GlowCommand.GLOW_COMMAND,
			AutoSignCommand.AUTO_SIGN_COMMAND,
			SetOwnerCommand.SET_OWNER_COMMAND
		), HelpCommand.HELP_COMMAND, "iso");

	private MainCommand(String name, List<ItemSorterCommand> children, ItemSorterCommand defaultCommand, String... aliases) {
		super(name, children, defaultCommand, aliases);
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		try {
			runCommand(s, label, args);
		} catch (CommandReturn ignored) {}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args){
		HashMap<String, String> commands = new HashMap<>();
		commands.put("help", "itemsorter.command.help");
		commands.put("list", "itemsorter.command.list");
		commands.put("base", "itemsorter.command.base");
		commands.put("filters", "itemsorter.command.filters");
		commands.put("deposits", "itemsorter.command.deposits");
		commands.put("glow", "itemsorter.command.glow");
		commands.put("autosign", "itemsorter.command.autosign");
		commands.put("reload", "itemsorter.command.reload");
		commands.put("setOwner", "itemsorter.command.setOwner");
		//  args.lenth == 4
		if(args.length == 4 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> page = new ArrayList<>();
			page.add("p:");
			return Utilities.searchforsimilarity(args[3], page);
		}else if(args.length == 4 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && ItemSorter.versionHandler.isGlowAvailable()){
			if(args[1].equalsIgnoreCase("filters")){
				ArrayList<String> filters = new ArrayList<>();
				filters.add("Trash");
				for(Material mat : Material.values()){
					filters.add(mat.name());
				}
				return Utilities.searchforsimilarity(args[3], filters);
			}
		}

		//  args.lenth == 3
		if(args.length == 3 && args[0].equalsIgnoreCase("list") && s.hasPermission(commands.get("list"))){
			ArrayList<String> page = new ArrayList<>();
			page.add("p:");
			return Utilities.searchforsimilarity(args[2], page);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> filters = new ArrayList<>();
			filters.add("item");
			filters.add("Trash");
			for(Material mat : Material.values()){
				filters.add(mat.name());
			}
			return Utilities.searchforsimilarity(args[2], filters);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("deposits") && s.hasPermission(commands.get("deposits"))){
			ArrayList<String> page = new ArrayList<>();
			page.add("p:");
			return Utilities.searchforsimilarity(args[2], page);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && ItemSorter.versionHandler.isGlowAvailable()){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[2], bases);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("autosign") && s.hasPermission(commands.get("autosign"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[2], bases);
		}else if(args.length == 3 && args[0].equalsIgnoreCase("setOwner") && s.hasPermission(commands.get("setOwner"))){
			ArrayList<String> players = new ArrayList<>();
			for(Player p : Bukkit.getOnlinePlayers()){
				players.add(p.getName());
			}
			return Utilities.searchforsimilarity(args[2], players);
		}

		//  args.lenth == 2
		if(args.length == 2 && args[0].equalsIgnoreCase("list") && s.hasPermission(commands.get("list"))){
			ArrayList<String> owners = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				owners.add(sys.getOwnerName());
			}
			return Utilities.searchforsimilarity(args[1], owners);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("base") && s.hasPermission(commands.get("base"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("deposits") && s.hasPermission(commands.get("deposits"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && ItemSorter.versionHandler.isGlowAvailable()){
			ArrayList<String> strings = new ArrayList<>();
			strings.add("filters");
			strings.add("base");
			strings.add("deposits");
			return Utilities.searchforsimilarity(args[1], strings);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("autosign") && s.hasPermission(commands.get("autosign"))){
			ArrayList<String> strings = new ArrayList<>();
			strings.add("filter");
			strings.add("deposit");
			strings.add("stop");
			return Utilities.searchforsimilarity(args[1], strings);
		}else if(args.length == 2 && args[0].equalsIgnoreCase("setOwner") && s.hasPermission(commands.get("setOwner"))){
			ArrayList<String> bases = new ArrayList<>();
			for(System sys : DataManager.getSystems()){
				bases.add(sys.name);
			}
			return Utilities.searchforsimilarity(args[1], bases);
		}

		//  args.lenth == 1
		if(args.length == 1){
			ArrayList<String> list = new ArrayList<>();
			for(String key : commands.keySet()){
				if(s.hasPermission(commands.get(key))){
					list.add(key);
				}
			}
			return Utilities.searchforsimilarity(args[0], list);
		}else{
			return null;
		}
	}
}
