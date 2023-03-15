package me.clcondorcet.itemsorter.events;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author clcondorcet
 */
public class OpenInventoryEvent implements Listener {

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e){
        if(ItemSorter.configManager.config.orderChestContent){
            Chest[] chests = new Chest[2];
            InventoryHolder barrel = null;
            boolean isBarrel = false;
            if(e.getInventory().getHolder() instanceof DoubleChest){
                chests[0] = (Chest) ((DoubleChest) e.getInventory().getHolder()).getLeftSide();
                chests[1] = (Chest) ((DoubleChest) e.getInventory().getHolder()).getRightSide();
            }else if(e.getInventory().getHolder() instanceof Chest){
                chests = new Chest[1];
                chests[0] = ((Chest) e.getInventory().getHolder());
            }else if(ItemSorter.versionHandler.instanceOfBarel(e.getInventory().getHolder())){
                isBarrel = true;
                barrel = e.getInventory().getHolder();
            }else{
                return;
            }
            if(isBarrel){
                reArrange(barrel, ((Barrel)barrel).getLocation());
            }else{
                for(Chest block : chests){
                    reArrange(block, block.getLocation());
                }
            }
        }
    }

    private void reArrange(InventoryHolder invHold, Location location){
        FutureLocation loc = new FutureLocation(location);
        Filter filter = DataManager.filter.get(loc);
        if (filter != null) {
            HashMap<Material, ArrayList<ItemStack>> items = new HashMap<>();
            for(ItemStack item : invHold.getInventory().getContents()){
                try{
                    if(item.getType() != Material.AIR){
                        if(items.containsKey(item.getType())){
                            items.get(item.getType()).add(item);
                        }else{
                            ArrayList<ItemStack> itemArray = new ArrayList<>();
                            itemArray.add(item);
                            items.put(item.getType(), itemArray);
                        }
                        invHold.getInventory().remove(item);
                    }
                }catch(Exception ignored){}
            }
            for(Material mat : items.keySet()){
                for(ItemStack item : items.get(mat)){
                    invHold.getInventory().addItem(item);
                }
            }
        }
    }

}
