package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WorldUnloadEvent implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWorldUnload(org.bukkit.event.world.WorldUnloadEvent e){
        Runnable runnable = () -> {
            for (System sys : DataManager.getSystems()) {
                if (sys.baseLoc.getWorldName().equals(e.getWorld().getName())) {
                    DataManager.setNotLoadedSystem(sys);
                    ItemSorter.getInstance().getLogger().info("Base system '" + sys.name + "' has been unloaded since world " + e.getWorld().getName() + " has been unloaded.");
                }
            }
        };
        if (e.isAsynchronous()) {
            Bukkit.getScheduler().runTask(ItemSorter.getInstance(), runnable);
        } else {
            runnable.run();
        }
    }
}
