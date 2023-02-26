package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.utils.Utilities;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author clcondorcet
 */
public class BlockBreakEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
        if(!Utilities.isContainerOrBaseBlock(e.getBlock().getType()) && !Utilities.isSign(e.getBlock())){
            return;
        }
        Location blockLoc = e.getBlock().getLocation();
        for(System sys : DataManager.getSystems()){
            if(sys.isSameBlock(blockLoc)){
                if (sys.hasOwnerPermission(e.getPlayer())) {
                    sys.delete(true, true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_baseDeleted);
                }else{
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needOwnerToBreak);
                }
                return;
            }
            Filter filter = sys.getFilterWithBlock(e.getBlock());
            if (filter != null) {
                if(sys.canAccess(e.getPlayer())){
                    filter.delete(true, true, true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_filterDeleted);
                }else{
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needTrustBreakFilter);
                }
            }
            Deposit deposit = sys.getDepositWithBlock(e.getBlock());
            if (deposit != null) {
                if(sys.canAccess(e.getPlayer())){
                    deposit.delete(true, true, true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_depositDeleted);
                }else{
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needTrustBreakDeposit);
                }
            }
        }
        for (System sys: DataManager.getLoadingSystems()) {
            if (sys.isSameBlock(blockLoc)) {
                if (sys.hasOwnerPermission(e.getPlayer())) {
                    DataManager.removeLoadingSystem(sys);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_baseDeleted);
                } else {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needOwnerToBreak);
                }
                return;
            }
        }
    }
}
