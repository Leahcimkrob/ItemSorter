package me.clcondorcet.itemsorter.listeners.dependencies;

import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import us.lynuxcraft.deadsilenceiv.advancedchests.events.ChestRemoveEvent;

public class AdvancedChestRemoveEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(ChestRemoveEvent e) {
        // TODO VERIFY IF THE API HAS CHANGED TO HAVE PLAYER IN THE EVENT !!
        // TODO IF YES, JUST DO : e.setCancelled(onBlockBreak(e.getChest().getLocation().getBlock(), e.getPlayer()));

        Block block = e.getChest().getLocation().getBlock();
        if(!Utilities.isContainerOrBaseBlock(block.getType())){
            return;
        }

        FutureLocation blockLoc = new FutureLocation(block.getLocation());
        Filter filter = DataManager.filter.get(blockLoc);
        if (filter != null) {
            e.setCancelled(true);
            return;
        }

        Deposit deposit = DataManager.deposits.get(blockLoc);
        if (deposit != null) {
            e.setCancelled(true);
        }
    }
}
