package me.clcondorcet.itemSorter.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {

	private HashMap<String, FileConfiguration> configs = new HashMap<String, FileConfiguration>();
	public Config config;
	public Messages messages;

	public ConfigManager(){}

	public void loadConfigs(){
		configs.clear();
		Plugin plug = Main.getInstance();
		load(plug.getDataFolder(), "config.yml");
		config = new Config(getConfig("config.yml"), loadDirect(new File(Main.getInstance().getDataFolder(), "temp.yml"), "config.yml"));
		List<String> msgFiles = Arrays.asList("EN", "FR", "DE");
		boolean isOther = true;
		for(String st : msgFiles){
			if(config.language.equalsIgnoreCase(st)){
				isOther = false;
				loadMessage(new File(Main.getInstance().getDataFolder(), "translations"), "messages_" + st + ".yml", true);
				messages = new Messages(getConfig("messages_" + st + ".yml"), loadDirect(new File(Main.getInstance().getDataFolder(), "temp.yml"), "translations/messages_" + st + ".yml"));
			}else{
				replace(new File(Main.getInstance().getDataFolder(), "translations"), "messages_" + st + ".yml", "translations/messages_" + st + ".yml");
			}
		}
		if(isOther){
			loadMessage(new File(Main.getInstance().getDataFolder(), "translations"), "messages_" + config.language.toUpperCase() + ".yml", false);
			messages = new Messages(getConfig("messages_" + config.language.toUpperCase() + ".yml"), loadDirect(new File(Main.getInstance().getDataFolder(), "temp.yml"), "translations/messages_EN.yml"));
			Main.log.info("Loading messages files from a non register language. If the file doesn't exist it will be copy from messages_EN.yml");
		}
		load(plug.getDataFolder(), "data.yml");
	}

	public void load(File parent, String fileName){
		Plugin plug = Main.getInstance();
		try {
			if (!plug.getDataFolder().exists()) {
	            plug.getDataFolder().mkdir();
	        }
			File file = new File(parent, fileName);
			if(!file.exists()){
				InputStream in = plug.getResource(fileName);
		        OutputStream out;
		        out = new FileOutputStream(file);
		        byte[] buffer = new byte[1024];
		        int len;
		        while ((len = in.read(buffer)) != -1) {
		        	out.write(buffer, 0, len);
			    }
		        in.close();
		        out.close();
		       
			}
			 FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		     configs.put(fileName, config);
		}catch(Exception ex){
			ex.printStackTrace();
			Main.getInstance().getLogger().severe("Disable due to configuration error ! check config.yml or delete the file.");
			plug.getPluginLoader().disablePlugin(plug);
		}
	}
	
	public FileConfiguration loadDirect(File file, String fileName){
		Plugin plug = Main.getInstance();
		try {
			file.delete();
			if(!file.exists()){
				InputStream in = plug.getResource(fileName);
		        OutputStream out = new FileOutputStream(file);
		        byte[] buffer = new byte[1024];
		        int len;
		        while ((len = in.read(buffer)) != -1) {
		        	out.write(buffer, 0, len);
			    }
		        in.close();
		        out.close();
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			file.delete();
			return config;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	public void loadMessage(File parent, String fileName, boolean inRessource){
		Plugin plug = Main.getInstance();
		try {
			if(!parent.exists()){
				parent.mkdirs();
			}
			File file = new File(parent, fileName);
			if(!file.exists()){
				InputStream in = null;
				if(inRessource){
					in = plug.getResource("translations/" + fileName);
				}else{
					in = plug.getResource("translations/messages_EN.yml");
				}
				OutputStream out;
				out = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				in.close();
				out.close();
			}
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			configs.put(fileName, config);
		}catch(Exception ex){
			ex.printStackTrace();
			Main.getInstance().getLogger().severe("Disable due to configuration error ! check config.yml or delete the file.");
			plug.getPluginLoader().disablePlugin(plug);
		}
	}

	public void replace(File parent, String fileName, String ressource){
		try{
			if(!parent.exists()){
				parent.mkdirs();
			}
			File file = new File(parent, fileName);
			file.delete();
			InputStream in = Main.getInstance().getResource(ressource);
			OutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig(String conf){
		if(configs.containsKey(conf)){
			return configs.get(conf);
		}else{
			load(Main.getInstance().getDataFolder(), conf);
			return configs.get(conf);
		}
	}
	
	public void saveConfig(String config, File parent){
		File file = new File(parent, config);
		try {
			getConfig(config).save(file);
		} catch (Exception e) {
			Main.getInstance().getLogger().severe("Error saving file");
			e.printStackTrace();
		}
	}
	
	public void createConfig(String config, File parent){
		File file = new File(parent, config);
		FileConfiguration config2 = YamlConfiguration.loadConfiguration(file);
		configs.put(config, config2);
	}
}
