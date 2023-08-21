package me.clcondorcet.itemsorter.dependencies;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.listeners.dependencies.AdvancedChestRemoveEvent;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.lynuxcraft.deadsilenceiv.advancedchests.AdvancedChestsAPI;
import us.lynuxcraft.deadsilenceiv.advancedchests.chest.AdvancedChest;

import java.util.ArrayList;
import java.util.Collections;

public class AdvancedChestsDependency {

    private static boolean alreadyLoaded = false;

    public static boolean isAPILoaded() {
        boolean isAPILoaded = Bukkit.getPluginManager().isPluginEnabled("AdvancedChests");
        if (isAPILoaded && !alreadyLoaded) {
            Bukkit.getPluginManager().registerEvents(new AdvancedChestRemoveEvent(), ItemSorter.getInstance());
            alreadyLoaded = true;
        }
        return isAPILoaded;
    }

    public static AdvancedChest getAdvancedChest(FutureLocation location) {
        if (!isAPILoaded()) return null;
        return AdvancedChestsAPI.getChestManager().getAdvancedChest(location.getLocation());
    }

    public static AdvancedChest getAdvancedChest(Location location) {
        if (!isAPILoaded()) return null;
        return AdvancedChestsAPI.getChestManager().getAdvancedChest(location);
    }

    public static AdvancedChest getAdvancedChest(Inventory inventory) {
        if (!isAPILoaded()) return null;
        return AdvancedChestsAPI.getInventoryManager().getAdvancedChest(inventory);
    }

    public static boolean isRealInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            if (!isAPILoaded()) return true;
            AdvancedChest aChest = getAdvancedChest(event.getInventory());
            if (aChest == null) return true;
            if (aChest.isPlayerChangingPage(((Player)event.getPlayer()))) return false;
            return true;
        }
        return true;
    }

    public static ArrayList<ItemStack> addItem(AdvancedChest aChest, ItemStack itemStack) {
        if (aChest == null) return new ArrayList<>(Collections.singletonList(itemStack));
        boolean isInserted = aChest.getChestType().getDispenserService().dispenseItemToChest(aChest, itemStack);
        if (!isInserted) {
            return new ArrayList<>(Collections.singleton(itemStack));
        } else {
            return new ArrayList<>();
        }
    }
}
