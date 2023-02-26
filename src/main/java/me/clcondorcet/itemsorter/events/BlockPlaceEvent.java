package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static me.clcondorcet.itemsorter.events.EventsManager.*;

/**
 * @author clcondorcet
 */
public class BlockPlaceEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlaceEvent(org.bukkit.event.block.BlockPlaceEvent e) {
        if(playerInAutoCache(e.getPlayer())){
            if(ItemSorter.versionHandler.isWallSign(e.getBlockPlaced())){
                Bukkit.getScheduler().runTaskLater(ItemSorter.getInstance(), () -> e.getPlayer().closeInventory(), 0);
            }
        }
    }

}
