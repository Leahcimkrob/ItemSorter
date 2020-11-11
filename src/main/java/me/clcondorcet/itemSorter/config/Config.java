package me.clcondorcet.itemSorter.config;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public int radius;
    public int verticalRadius;
    public int maxBases_default;
    public HashMap<String, Integer> maxBases_others;
    public boolean orderChestContent;
    public String language;

    public Config(FileConfiguration conf, FileConfiguration source){
        this.radius = (int) getOrDefault(conf, "radius", source.get("radius"));
        this.verticalRadius = (int) getOrDefault(conf, "verticalRadius", source.get("verticalRadius"));
        this.maxBases_default = (int) getOrDefault(conf, "maxBases.default", source.get("maxBases.default"));
        List<Map<?, ?>> map = (List<Map<?, ?>>) getOrDefault(conf, "maxBases.others", source.get("maxBases.others"));
        this.maxBases_others = new HashMap<>();
        try {
            for (Map<?, ?> element : map) {
                for (Object key : element.keySet()) {
                    try {
                        this.maxBases_others.put(key.toString(), (Integer) element.get(key));
                    } catch (Exception ex) {
                        Main.log.severe("There is an error in the config file at: MaxBases.others please try fix it or restore it !");
                        Main.log.severe("It failed at: " + element.toString());
                        throw ex;
                    }
                }
            }
        }catch(Exception ex){
            Main.log.severe("The error:");
            ex.printStackTrace();
        }
        this.orderChestContent = (boolean) getOrDefault(conf, "orderChestContent", source.get("orderChestContent"));
        this.language = (String) getOrDefault(conf, "Language", source.get("Language"));

        try{
            Main.configManager.saveConfig("config.yml", Main.getInstance().getDataFolder());
        }catch(Exception ex) {
            Main.log.severe("Error when loading the config file.");
            ex.printStackTrace();
        }
    }

    public Object getOrDefault(FileConfiguration conf, String path, Object value){
        Object a = conf.get(path);
        if(a == null || a.getClass() != value.getClass()){
            conf.set(path, value);
            a = value;
        }
        return a;
    }
}