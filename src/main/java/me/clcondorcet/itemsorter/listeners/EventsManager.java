package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author clcondorcet
 */
public class EventsManager {

    public static HashMap<Player, System> autofilters = new HashMap<>();
    public static HashMap<Player, System> autodeposits = new HashMap<>();

    public static boolean playerInAutoCache(Player p) {
        return autofilters.containsKey(p) || autodeposits.containsKey(p);
    }

    public static void loadEvents(ItemSorter plugin) {
        Bukkit.getPluginManager().registerEvents(new MoveItemEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new GuiEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlockBreakEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new ExplosionEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new SignChangeEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new WorldLoadEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new WorldUnloadEvent(), plugin);
        Bukkit.getPluginManager().registerEvents(new OpenInventoryEvent(), plugin);
        ItemSorter.versionHandler.newEvent(plugin);
    }
}
