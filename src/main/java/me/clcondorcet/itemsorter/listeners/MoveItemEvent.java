package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.events.FakeInventoryMoveItemEvent;
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
		if (e instanceof FakeInventoryMoveItemEvent) return;
		ItemTransferTick.getInstance().addContainerWaitingForTransfer(e.getDestination());
	}
}
