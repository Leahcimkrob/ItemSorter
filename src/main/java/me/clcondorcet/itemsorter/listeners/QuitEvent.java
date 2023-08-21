package me.clcondorcet.itemsorter.listeners;

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
        autofilters.remove(e.getPlayer());
        autodeposits.remove(e.getPlayer());
    }

}
