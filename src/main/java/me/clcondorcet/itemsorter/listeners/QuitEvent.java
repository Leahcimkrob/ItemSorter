package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.data.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.clcondorcet.itemsorter.listeners.EventsManager.autodeposits;
import static me.clcondorcet.itemsorter.listeners.EventsManager.autofilters;

/**
 * @author clcondorcet
 */
public class QuitEvent implements Listener {

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e){
        DataManager.unregisterOnlinePlayer(e.getPlayer().getName());
        autofilters.remove(e.getPlayer());
        autodeposits.remove(e.getPlayer());
    }

}
