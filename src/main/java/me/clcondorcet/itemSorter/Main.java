package me.clcondorcet.itemSorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.clcondorcet.itemSorter.Commands.Commands;
import me.clcondorcet.itemSorter.Events.GuiEvent;
import me.clcondorcet.itemSorter.Objects.CachedItems;
import me.clcondorcet.itemSorter.Objects.Deposit;
import me.clcondorcet.itemSorter.Objects.Filter;
import me.clcondorcet.itemSorter.Objects.System;
import me.clcondorcet.itemSorter.config.ConfigManager;
import me.clcondorcet.itemSorter.Events.Event;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	/**
	 * 
	 * @author clcondorcet
	 */
	
	private static Plugin plugin;
	public static Logger log;
	public static String prefix = "§6IS> ";
	public static ArrayList<System> bases = new ArrayList<>();
	public static HashMap<String, ArrayList<String>> notLoadedBases = new HashMap<>();
	public static String version;
	public static VersionHandler versionHandler;
	public static VersionChecker versionChecker;
	public static CachedItems cachedItems;
	public static ConfigManager configManager;
	public static HashMap<Player, Object[]> inFilter = new HashMap<>();
	public static HashMap<Player, Object[]> inSystem = new HashMap<>();
	
	public static Plugin getInstance(){
		return plugin;
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		log = this.getLogger();
		versionHandler = new VersionHandler();
		versionChecker = new VersionChecker();
		configManager = new ConfigManager();
		configManager.loadConfigs();
		prefix = Main.configManager.config.msg_prefix.replaceAll("&", "§");
		loadEvents();
		loadCommands();
		loadConfigs(null);
		cachedItems = new CachedItems();
	}
	
	public static void loadConfigs(CommandSender s){
		Main.bases.clear();
		FileConfiguration config = Main.configManager.getConfig("data.yml");
		ArrayList<String> systems = new ArrayList<>();
		try {
			for(String st : config.getConfigurationSection("data").getKeys(false)){
				systems.add(st);
			}
		}catch(Exception ignored) {}
		ArrayList<System> toDelete = new ArrayList<>();
		for(String st : systems){
			try{
				System sys = loadSysFromFile(config, st);
				if(sys.baseLoc.getBlock().getType() != Material.ENDER_CHEST || !(Main.versionHandler.isWallSign(sys.sign.getBlock()))){
					toDelete.add(sys);
				}else{
					bases.add(sys);
				}
			}catch(NullPointerException ex){
				if(s != null){
					s.sendMessage(Main.prefix + "§4Error when loading the storage system §6\"" + st + "\" §4Please check the console !");
					Main.log.severe("Error when loading the storage system named " + st + " from data.yml !");
					Main.log.severe("Did you modify the data.yml file ? Or did you change the world name ?");
					Main.log.severe("The storage system will not be loaded but will not be removed from the file data.yml in order to fix it if you can.");
					Main.log.severe("Loading the next one now ...");
				}else{
					String worldName = config.getString("data." + st + ".baseLoc").split(":")[0];
					if(!notLoadedBases.containsKey(worldName)){
						notLoadedBases.put(worldName, new ArrayList<String>());
					}
					notLoadedBases.get(worldName).add(st);
					Main.log.warning("The system storage system named " + st + " gets an error when loading. We will wait until the world gets loaded...");
				}
			}catch(Exception ex){
				Main.log.severe("This error may not occur please contact clcondorcet and sho him the this message and the error below:");
				ex.printStackTrace();
				Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
				return;
			}
		}
		for(System sys : toDelete){
			Main.log.info("The base " + sys.name + " will be deleted because the base is not longer at the same location.");
			sys.delete();
		}
	}

	public static System loadSysFromFile(FileConfiguration config, String st){
		String baseLoc = config.getString("data." + st + ".baseLoc");
		String sign = config.getString("data." + st + ".sign");
		String owner = config.getString("data." + st + ".owner");
		ArrayList<String> trusts = new ArrayList<>();
		for(String trust : config.getStringList("data." + st + ".trusts")){
			trusts.add(trust);
		}
		ArrayList<Filter> filters = new ArrayList<>();
		try{
			for(String key : config.getConfigurationSection("data." + st + ".filters").getKeys(false)){
				try{
					Location containerLoc = System.getLoc(config.getString("data." + st + ".filters." + key + ".loc"));
					Location signLoc = System.getLoc(config.getString("data." + st + ".filters." + key + ".sign"));
					if(Utilities.isDF(containerLoc.getBlock().getType()) && Utilities.isSign(signLoc.getBlock())){
						if(config.getBoolean("data." + st + ".filters." + key + ".isTrash")){
							filters.add(new Filter(containerLoc, signLoc, config.getInt("data." + st + ".filters." + key + ".TrashPriority")));
						}else{
							filters.add(new Filter(containerLoc, signLoc, config.getStringList("data." + st + ".filters." + key + ".materials")));
						}
					}
				}catch(Exception ignored){}
			}
		}catch(Exception ignored){}
		ArrayList<Deposit> deposits = new ArrayList<>();
		try{
			for(String key : config.getConfigurationSection("data." + st + ".deposits").getKeys(false)){
				try{
					Location containerLoc = System.getLoc(config.getString("data." + st + ".deposits." + key + ".loc"));
					Location signLoc = System.getLoc(config.getString("data." + st + ".deposits." + key + ".sign"));
					if(Utilities.isDF(containerLoc.getBlock().getType()) && Utilities.isSign(signLoc.getBlock())){
						deposits.add(new Deposit(containerLoc, signLoc));
					}
				}catch(Exception ignored){}
			}
		}catch(Exception ignored){}
		return new System(st, baseLoc, sign, owner, trusts, filters, deposits);
	}

	public void loadEvents(){
		Bukkit.getServer().getPluginManager().registerEvents(new Event(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GuiEvent(), this);
		Main.versionHandler.newEvent(this);
	}

	public void loadCommands(){
		//this.getCommand("IS").setExecutor(new Commands());
		this.getCommand("ItemSorter").setExecutor(new Commands());
		//this.getCommand("IS").setTabCompleter(new Commands());
	}
}
