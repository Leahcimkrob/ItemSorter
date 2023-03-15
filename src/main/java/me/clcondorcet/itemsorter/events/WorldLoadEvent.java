package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

/**
 * @author clcondorcet
 */
public class WorldLoadEvent implements Listener {

    @EventHandler
    public void onWorldLoad(org.bukkit.event.world.WorldLoadEvent e){
        Runnable runnable = () -> {
            ArrayList<System> toDelete = new ArrayList<>();
            if (DataManager.systemToCheck.containsKey(e.getWorld().getName())) {
                for (System sys : (ArrayList<System>) DataManager.systemToCheck.get(e.getWorld().getName()).clone()) {
                    try {
                        DataManager.removeSystem(sys);
                        if (sys.checkExistsInWorld()) {
                            DataManager.addSystem(sys);
                            for (Deposit depo : sys.getAllDeposits()) {
                                if (!depo.checkExistsInWorld()) {
                                    depo.delete(true, false, true);
                                }
                            }
                            for (Filter filter : sys.getAllFilters()) {
                                if (!filter.checkExistsInWorld()) {
                                    filter.delete(true, false, true);
                                }
                            }
                        } else {
                            toDelete.add(sys);
                        }
                    } catch (FutureLocation.WorldNotLoaded ex) {
                        ItemSorter.getInstance().getLogger().warning("Caught error in WorldLoadEvent. The world is still not loaded?");
                    }
                }
            }
            for(System sys : toDelete){
                ItemSorter.getInstance().getLogger().info("The base " + sys.name + " will be deleted because the base is not longer at the same location.");
                sys.delete(true, false);
            }
        };
        if (e.isAsynchronous()) {
            Bukkit.getScheduler().runTask(ItemSorter.getInstance(), runnable);
        } else {
            runnable.run();
        }
    }

}
