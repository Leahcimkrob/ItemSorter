package me.clcondorcet.itemsorter.processing;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ItemTransferTick {

    private static ItemTransferTick instance = null;

    public static ItemTransferTick getInstance() {
        if (instance == null) {
            instance = new ItemTransferTick();
        }
        return instance;
    }

    private final HashSet<Inventory> containersWaitingForTransfer = new HashSet<>();

    private ItemTransferTick() {}

    public boolean addContainerWaitingForTransfer(Inventory inv) {
        if (instance != null) {
            return instance.containersWaitingForTransfer.add(inv);
        }
        return false;
    }

    public boolean removeContainerWaitingForTransfer(Inventory inv) {
        if (instance != null) {
            return instance.containersWaitingForTransfer.remove(inv);
        }
        return false;
    }

    private HashMap<System, HashMap<Material, HashMap<Integer, Filter>>> filterCache = new HashMap<>();
    private HashMap<System, HashMap<Integer, Filter>> filterTrashCache = new HashMap<>();

    private void addFilterCache(System system, Material mat, HashMap<Integer, Filter> filters) {
        if (!filterCache.containsKey(system)) filterCache.put(system, new HashMap<>());
        HashMap<Material, HashMap<Integer, Filter>> cache2 = filterCache.get(system);
        cache2.put(mat, filters);
    }

    public HashMap<Integer, Filter> getFilterCached(System sys, Material mat) {
        if (!filterCache.containsKey(sys) || !filterCache.get(sys).containsKey(mat))
            addFilterCache(sys, mat, sys.getfilters(sys, mat));
        return filterCache.get(sys).get(mat);
    }

    private void addFilterTrashCache(System system, HashMap<Integer, Filter> filters) {
        filterTrashCache.put(system, filters);
    }

    public HashMap<Integer, Filter> getTrashCached(System sys) {
        if (!filterTrashCache.containsKey(sys))
            addFilterTrashCache(sys, sys.getfilters(sys));
        return filterTrashCache.get(sys);
    }

    public void transferItems () {
        for (Inventory inv : containersWaitingForTransfer) {
            transfer(inv);
        }

        containersWaitingForTransfer.clear();
        filterCache.clear();
    }

    public void transfer(Inventory inv) {
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

    private void add(InventoryHolder invHold, Location location) {
        FutureLocation loc = new FutureLocation(location);
        Deposit deposit = DataManager.deposits.get(loc);
        if (deposit != null) {
            System sys = deposit.sys;
            ArrayList<ItemStack> items = new ArrayList<>();
            for (ItemStack item : invHold.getInventory().getContents()) {
                try {
                    if(item.getType() != Material.AIR) {
                        items.add(item);
                        invHold.getInventory().remove(item);
                    }
                } catch(Exception ignored) {}
            }
            ArrayList<ItemStack> result = sys.addItems(items);
            for (ItemStack itemToReEnter : result) {
                invHold.getInventory().addItem(itemToReEnter);
            }
        }
    }
}
