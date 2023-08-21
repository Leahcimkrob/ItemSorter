package me.clcondorcet.itemsorter.processing;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.Bukkit;

public class ItemSorterTick {

    private static ItemSorterTick instance = null;

    public static ItemSorterTick getInstance() {
        if (instance == null) {
            instance = new ItemSorterTick();
        }
        return instance;
    }

    private final int taskId;

    private ItemSorterTick() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(ItemSorter.getInstance(), this::runItemSorterTick, 0L, 1L);
    }

    public void unload() {
        Bukkit.getScheduler().cancelTask(taskId);
        instance = null;
    }

    private void runItemSorterTick() {
        try {
            ItemTransferTick.getInstance().transferItems();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
