package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author clcondorcet
 */
public class BlockBreakEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
        e.setCancelled(onBlockBreak(e.getBlock(), e.getPlayer()));
    }

    protected boolean onBlockBreak(Block block, Player player) {
        if(!Utilities.isContainerOrBaseBlock(block.getType()) && !Utilities.isSign(block)){
            return false;
        }
        FutureLocation blockLoc = new FutureLocation(block.getLocation());
        System sys = DataManager.bases.get(blockLoc);
        if (sys != null) {
            if (sys.hasOwnerPermission(player)) {
                if (DataManager.getLoadingSystems().contains(sys)) {
                    DataManager.removeLoadingSystem(sys);
                } else {
                    sys.delete(true, true);
                }
                player.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_baseDeleted);
            } else {
                player.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needOwnerToBreak);
                return true;
            }
            return false;
        }

        Filter filter = DataManager.filter.get(blockLoc);
        if (filter != null) {
            sys = filter.sys;
            if (sys.canAccess(player)) {
                filter.delete(true, true, true);
                player.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_filterDeleted);
            } else {
                player.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needTrustBreakFilter);
                return true;
            }
            return false;
        }

        Deposit deposit = DataManager.deposits.get(blockLoc);
        if (deposit != null) {
            sys = deposit.sys;
            if (sys.canAccess(player)) {
                deposit.delete(true, true, true);
                player.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_depositDeleted);
            } else {
                player.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_needTrustBreakDeposit);
                return true;
            }
        }
        return false;
    }
}
