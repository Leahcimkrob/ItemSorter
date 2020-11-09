package me.clcondorcet.itemSorter.Events;

import me.clcondorcet.itemSorter.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class EventServerLoad implements Listener {

    @EventHandler
    public void onServerFinishedLoading(ServerLoadEvent e){
        for(String world : Main.notLoadedBases.keySet()){
            for(String sys : Main.notLoadedBases.get(world)){
                Main.log.warning("Error when loading the storage system named " + sys + " from data.yml !");
                Main.log.warning("Did you modify the data.yml file ? Or did you change the world name ?");
                Main.log.warning("The storage system will not be loaded but will not be removed from the file data.yml in order to fix it if you can.");
                Main.log.warning("If the world " + world + " is loaded, it will load properly.");
            }
        }
    }

}
