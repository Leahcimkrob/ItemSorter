package me.clcondorcet.itemsorter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.clcondorcet.itemsorter.events.EventsManager.autodeposits;
import static me.clcondorcet.itemsorter.events.EventsManager.autofilters;

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
