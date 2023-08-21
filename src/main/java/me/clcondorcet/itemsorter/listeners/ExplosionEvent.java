package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author clcondorcet
 */
public class ExplosionEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(EntityExplodeEvent e){
        if(e.isCancelled()) {
            return;
        }
        for(Block block : e.blockList()){
            if(!Utilities.isSign(block) && !Utilities.isContainerOrBaseBlock(block.getType())){
                continue;
            }

            FutureLocation blockLoc = new FutureLocation(block.getLocation());
            System sys = DataManager.bases.get(blockLoc);
            if (sys != null) {
                sys.delete(true, true);
                continue;
            }

            Filter filter = DataManager.filter.get(blockLoc);
            if (filter != null) {
                filter.delete(true, true, true);
                continue;
            }

            Deposit deposit = DataManager.deposits.get(blockLoc);
            if (deposit != null) {
                deposit.delete(true, true, true);
            }
        }
    }
}
