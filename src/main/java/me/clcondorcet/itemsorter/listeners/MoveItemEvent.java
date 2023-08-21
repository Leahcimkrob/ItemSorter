package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.processing.ItemTransferTick;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

/**
 * @author clcondorcet
 */
public class MoveItemEvent implements Listener {
	@EventHandler
	public void onMoveItem(final InventoryMoveItemEvent e) {
		ItemTransferTick.getInstance().addContainerWaitingForTransfer(e.getDestination());
	}
}
