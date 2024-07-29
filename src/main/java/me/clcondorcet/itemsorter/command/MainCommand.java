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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static Predicate<String> startWith(String prefix) {
		return t -> t.toLowerCase().startsWith(prefix.toLowerCase());
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
			return Stream.of("p:").filter(startWith(args[3])).collect(Collectors.toList());
		}else if(args.length == 4 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && ItemSorter.versionHandler.isGlowAvailable()){
			if(args[1].equalsIgnoreCase("filters")){
				ArrayList<String> filters = new ArrayList<>();
				filters.add("Trash");
				for(Material mat : Material.values()){
					filters.add(mat.name());
				}
				return filters.stream().filter(startWith(args[3])).collect(Collectors.toList());
			}
		}

		//  args.lenth == 3
		if(args.length == 3 && args[0].equalsIgnoreCase("list") && s.hasPermission(commands.get("list"))){
			return Stream.of("p:").filter(startWith(args[2])).collect(Collectors.toList());
		}else if(args.length == 3 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			ArrayList<String> filters = new ArrayList<>();
			filters.add("item");
			filters.add("Trash");
			for(Material mat : Material.values()){
				filters.add(mat.name());
			}
			return filters.stream().filter(startWith(args[2])).collect(Collectors.toList());
		}else if(args.length == 3 && args[0].equalsIgnoreCase("deposits") && s.hasPermission(commands.get("deposits"))){
			return Stream.of("p:").filter(startWith(args[2])).collect(Collectors.toList());
		}else if(args.length == 3 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && ItemSorter.versionHandler.isGlowAvailable()){
			return DataManager.getSystems().stream()
					.filter(system -> !(s instanceof Player) || system.canAccess((Player) s))
					.map(system -> system.name)
					.filter(startWith(args[2]))
					.collect(Collectors.toList());
		}else if(args.length == 3 && args[0].equalsIgnoreCase("autosign") && s.hasPermission(commands.get("autosign"))){
			return DataManager.getSystems().stream()
					.filter(system -> !(s instanceof Player) || system.canAccess((Player) s))
					.map(system -> system.name)
					.filter(startWith(args[2]))
					.collect(Collectors.toList());
		}else if(args.length == 3 && args[0].equalsIgnoreCase("setOwner") && s.hasPermission(commands.get("setOwner"))){
			return Bukkit.getOnlinePlayers().stream()
					.filter(p -> !(s instanceof Player) || ((Player) s).canSee(p))
					.map(HumanEntity::getName)
					.filter(startWith(args[2]))
					.collect(Collectors.toList());
		}

		//  args.lenth == 2
		if(args.length == 2 && args[0].equalsIgnoreCase("list") && s.hasPermission(commands.get("list"))){
			return DataManager.getSystems().stream()
					.map(System::getOwnerName)
					.filter(startWith(args[1]))
					.collect(Collectors.toList());
		}else if(args.length == 2 && args[0].equalsIgnoreCase("base") && s.hasPermission(commands.get("base"))){
			return DataManager.getSystems().stream()
					.filter(system -> !(s instanceof Player) || system.canAccess((Player) s))
					.map(system -> system.name)
					.filter(startWith(args[1]))
					.collect(Collectors.toList());
		}else if(args.length == 2 && args[0].equalsIgnoreCase("filters") && s.hasPermission(commands.get("filters"))){
			return DataManager.getSystems().stream()
					.filter(system -> !(s instanceof Player) || system.canAccess((Player) s))
					.map(system -> system.name)
					.filter(startWith(args[1]))
					.collect(Collectors.toList());
		}else if(args.length == 2 && args[0].equalsIgnoreCase("deposits") && s.hasPermission(commands.get("deposits"))){
			return DataManager.getSystems().stream()
					.filter(system -> !(s instanceof Player) || system.canAccess((Player) s))
					.map(system -> system.name)
					.filter(startWith(args[1]))
					.collect(Collectors.toList());
		}else if(args.length == 2 && args[0].equalsIgnoreCase("glow") && s.hasPermission(commands.get("glow")) && ItemSorter.versionHandler.isGlowAvailable()){
			return Stream.of("filters", "base", "deposits").filter(startWith(args[1])).collect(Collectors.toList());
		}else if(args.length == 2 && args[0].equalsIgnoreCase("autosign") && s.hasPermission(commands.get("autosign"))){
			return Stream.of("deposit","filter","stop").filter(startWith(args[1])).collect(Collectors.toList());
		}else if(args.length == 2 && args[0].equalsIgnoreCase("setOwner") && s.hasPermission(commands.get("setOwner"))){
			return DataManager.getSystems().stream()
					.filter(system -> !(s instanceof Player) || system.canAccess((Player) s))
					.map(system -> system.name)
					.filter(startWith(args[1]))
					.collect(Collectors.toList());
		}

		//  args.lenth == 1
		if(args.length == 1){
			return commands.entrySet().stream()
					.filter(command -> s.hasPermission(command.getValue()) && command.getKey().startsWith(args[0].toLowerCase()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());
		}else{
			return Collections.emptyList();
		}
	}
}
