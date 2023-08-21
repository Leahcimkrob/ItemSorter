package me.clcondorcet.itemsorter.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class FakeInventoryMoveItemEvent extends InventoryMoveItemEvent {
    /*
    public static ArrayList<ItemStack> addItem(Inventory destinationInventory, ItemStack itemStack) {
        Chest chest = null;
        Inventory fakeInventory = Bukkit.createInventory(chest, InventoryType.HOPPER);
        fakeInventory.setItem(0, itemStack);
        FakeInventoryMoveItemEvent fakeEvent = new FakeInventoryMoveItemEvent(fakeInventory, fakeInventory.getItem(0), destinationInventory, true);
        Bukkit.getPluginManager().callEvent(fakeEvent);
        Bukkit.getLogger().info("after fake event ->");
        ArrayList<ItemStack> result = new ArrayList<>();
        for(ItemStack i : fakeEvent.getSource().getContents()) {
            if (i != null && i.getType() != Material.AIR) {
                Bukkit.getLogger().info(i.getType() + " " + i.getAmount());
                result.add(i);
            }
        }
        Bukkit.getLogger().info(result.size() + "");
        return result;
    }
     */

    private FakeInventoryMoveItemEvent(Inventory sourceInventory, ItemStack itemStack, Inventory destinationInventory, boolean didSourceInitiate) {
        super(sourceInventory, itemStack, destinationInventory, didSourceInitiate);
    }
}
