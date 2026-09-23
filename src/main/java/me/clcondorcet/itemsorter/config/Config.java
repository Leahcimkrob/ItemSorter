package me.clcondorcet.itemsorter.config;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author clcondorcet
 */
public class Config {

    public int radius;
    public int verticalRadius;
    public int maxSorters;
    public double sorterPrice;
    public boolean orderChestContent;
    public String language;

    public Config(FileConfiguration conf, FileConfiguration source){
        this.radius = (int) getOrDefault(conf, "radius", source.get("radius"));
        this.verticalRadius = (int) getOrDefault(conf, "verticalRadius", source.get("verticalRadius"));
        conf.set("maxBases", null);
        this.maxSorters = (int) getOrDefault(conf, "max_sorters", source.get("max_sorters"));
        this.sorterPrice = ((Number) getOrDefault(conf, "price", source.get("price"))).doubleValue();
        this.orderChestContent = (boolean) getOrDefault(conf, "orderChestContent", source.get("orderChestContent"));
        this.language = (String) getOrDefault(conf, "Language", source.get("Language"));

        try{
            ItemSorter.configManager.saveConfig("config.yml", ItemSorter.getInstance().getDataFolder());
        }catch(Exception ex) {
            ItemSorter.getInstance().getLogger().severe("Error when loading the config file.");
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