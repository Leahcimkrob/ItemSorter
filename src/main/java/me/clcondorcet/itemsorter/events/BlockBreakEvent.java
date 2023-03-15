package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
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
        FutureLocation blockLoc = new FutureLocation(e.getBlock().getLocation());
        System sys = DataManager.bases.get(blockLoc);
        if (sys != null) {
            if (sys.hasOwnerPermission(e.getPlayer())) {
                if (DataManager.getLoadingSystems().contains(sys)) {
                    DataManager.removeLoadingSystem(sys);
                } else {
                    sys.delete(true, true);
                }
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_baseDeleted);
            }else{
                e.setCancelled(true);
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needOwnerToBreak);
            }
            return;
        }

        Filter filter = DataManager.filter.get(blockLoc);
        if (filter != null) {
            sys = filter.sys;
            if(sys.canAccess(e.getPlayer())){
                filter.delete(true, true, true);
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_filterDeleted);
            }else{
                e.setCancelled(true);
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needTrustBreakFilter);
            }
            return;
        }

        Deposit deposit = DataManager.deposits.get(blockLoc);
        if (deposit != null) {
            sys = deposit.sys;
            if(sys.canAccess(e.getPlayer())){
                deposit.delete(true, true, true);
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_depositDeleted);
            }else{
                e.setCancelled(true);
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needTrustBreakDeposit);
            }
        }
    }
}
