package me.clcondorcet.itemsorter.events;

import java.util.ArrayList;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author clcondorcet
 */
public class MoveItemEvent implements Listener{

	@EventHandler
	public void onMoveItem(final InventoryMoveItemEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try{
					transfer(e.getDestination());
				}catch(Exception ignored){}
			}
		}.runTaskLater(ItemSorter.getInstance(), 2L);
	}

	public static void transfer(Inventory inv) {
		try{
			if(inv.getHolder() != null){
				Chest[] chests = new Chest[2];
				InventoryHolder barrel = null;
				boolean isBarrel = false;
				if(inv.getHolder() instanceof DoubleChest){
					chests[0] = (Chest) ((DoubleChest) inv.getHolder()).getLeftSide();
					chests[1] = (Chest) ((DoubleChest) inv.getHolder()).getRightSide();
				}else if(inv.getHolder() instanceof Chest){
					chests[0] = ((Chest) inv.getHolder());
				}else if(ItemSorter.versionHandler.instanceOfBarel(inv.getHolder())){
					isBarrel = true;
					barrel = inv.getHolder();
				}else{
					return;
				}
				if(isBarrel){
					add(barrel, ((Barrel) barrel).getLocation());
				}else{
					for(Chest block : chests){
						add(block, block.getLocation());
					}
				}
			}
		}catch(NullPointerException ignored){
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
 	
	public static void add(InventoryHolder invHold, Location loc) {
		for(System sys : DataManager.getSystems()) {
			for(Deposit deposit : sys.getDeposits()) {
				if(deposit.isSameBlock(loc)) {
					ArrayList<ItemStack> items = new ArrayList<>();
					for(ItemStack item : invHold.getInventory().getContents()){
						try {
							if(item.getType() != Material.AIR) {
								items.add(item);
								invHold.getInventory().remove(item);
							}
						} catch(Exception ignored) {}
					}
					ArrayList<ItemStack> result = sys.addItems(items);
					for(ItemStack itemToReEnter : result) {
						invHold.getInventory().addItem(itemToReEnter);
					}
					return;
				}
			}
		}
	}
}
