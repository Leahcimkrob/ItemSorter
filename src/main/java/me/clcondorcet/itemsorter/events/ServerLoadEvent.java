package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author clcondorcet
 */
public class ServerLoadEvent implements Listener {

    @EventHandler
    public void onServerFinishedLoading(org.bukkit.event.server.ServerLoadEvent e){
        for(String world : DataManager.systemToCheck.keySet()){
            for(System sys : DataManager.systemToCheck.get(world)){
                ItemSorter.getInstance().getLogger().warning("Error when loading the storage system named " + sys.name + " !");
                ItemSorter.getInstance().getLogger().warning("Did you change the world name ?");
                ItemSorter.getInstance().getLogger().warning("The storage system will not be loaded but will not be removed from the database in order to fix it if you can.");
                ItemSorter.getInstance().getLogger().warning("If the world " + world + " finally load, the system will load properly.");
            }
        }
    }

}
