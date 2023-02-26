package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.data.DataManager;
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
            for(System sys : DataManager.getSystems()){
                if (sys.baseLoc.equals(block.getLocation()) || sys.sign.equals(block.getLocation())){
                    sys.delete(true, true);
                    continue;
                }
                Filter fil = sys.getFilterWithBlock(block);
                if (fil != null) {
                    fil.delete(true, true, true);
                } else {
                    Deposit depo = sys.getDepositWithBlock(block);
                    if (depo != null) {
                        depo.delete(true, true, true);
                    }
                }
            }
        }
    }
}
