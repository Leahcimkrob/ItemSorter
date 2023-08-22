package me.clcondorcet.itemsorter.data.tools;

import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.Bukkit;

/**
 * @author clcondorcet
 */
public interface SignRefreshable {

    void refreshSign(boolean enable);
    default void refreshSignAsync(boolean enable) {
        Bukkit.getScheduler().runTaskAsynchronously(
            ItemSorter.getInstance(),
            () -> Bukkit.getScheduler().runTask(ItemSorter.getInstance(), () -> this.refreshSign(enable))
        );
    }
}
