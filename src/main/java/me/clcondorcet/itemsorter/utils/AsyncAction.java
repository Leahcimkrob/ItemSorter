package me.clcondorcet.itemsorter.utils;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AsyncAction {

    private final Plugin plugin;

    public AsyncAction(Plugin plugin) {
        this.plugin = plugin;
    }

    public void async(ItemSorterRunnable runnable) {
        Runnable asyncAction = () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                ItemSorter.getInstance().getLogger().severe("Error caught in an asynchronous runnable. This may causes internal issues to the plugin.");
                e.printStackTrace();
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncAction);
    }

    public void async(ItemSorterRunnable runnable, ItemSorterRunnable errorCallback) {
        Runnable asyncAction = () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                ItemSorter.getInstance().getLogger().warning("Error caught in an asynchronous runnable. The error will be printed to debug if it causes a real problem to the plugin. This error may not causes internal issues to the plugin.");
                e.printStackTrace();
                sync(errorCallback);
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(plugin, asyncAction);
    }

    public void sync(ItemSorterRunnable runnable) {
        Runnable syncAction = () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                ItemSorter.getInstance().getLogger().severe("Error caught in an synchronous runnable. This may causes internal issues to the plugin.");
                e.printStackTrace();
            }
        };
        Bukkit.getScheduler().runTask(plugin, syncAction);
    }

    public void sync(ItemSorterRunnable runnable, ItemSorterRunnable errorCallback) {
        Runnable syncAction = () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                ItemSorter.getInstance().getLogger().warning("Error caught in a synchronous runnable. The error will be printed to debug if it causes a real problem to the plugin. This error may not causes internal issues to the plugin.");
                e.printStackTrace();
                sync(errorCallback);
            }
        };
        Bukkit.getScheduler().runTask(plugin, syncAction);
    }

    @FunctionalInterface
    public interface ItemSorterRunnable {
        public abstract void run() throws Throwable;
    }
}
