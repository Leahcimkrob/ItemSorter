package me.clcondorcet.itemsorter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import me.clcondorcet.itemsorter.command.MainCommand;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.database.DatabaseManager;
import me.clcondorcet.itemsorter.events.EventsManager;
import me.clcondorcet.itemsorter.multiversion.VersionHandler;
import me.clcondorcet.itemsorter.config.ConfigManager;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import me.clcondorcet.itemsorter.utils.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author clcondorcet
 */
public class ItemSorter extends JavaPlugin {

	private static ItemSorter plugin;
	public static String prefix = "§6IS> ";
	public static String version;
	public static VersionHandler versionHandler;
	public static VersionChecker versionChecker;
	public static ConfigManager configManager;
	public static final String LIB_FOLDER = "library";

	public DatabaseManager databaseManager;
	public AsyncAction asyncAction;

	public static ItemSorter getInstance(){
		return plugin;
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		versionHandler = new VersionHandler();
		versionChecker = new VersionChecker();
		configManager = new ConfigManager();
		asyncAction = new AsyncAction(this);
		configManager.loadConfigs();
		prefix = ItemSorter.configManager.messages.msg_prefix;
		EventsManager.loadEvents(this);
		try {
			databaseManager = new DatabaseManager(this);
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
			throw new RuntimeException(e);
		}
		if (databaseManager.isLoaded) {
			loadCommands();
			DataManager.loadData();
		}
	}

	public void loadCommands(){
		MainCommand cmd = MainCommand.MAIN_COMMAND;
		PluginCommand command = this.getCommand(cmd.name);
		assert command != null;
		command.setExecutor(cmd);
		command.setAliases(cmd.aliases);
		command.setTabCompleter(cmd);
	}
}
