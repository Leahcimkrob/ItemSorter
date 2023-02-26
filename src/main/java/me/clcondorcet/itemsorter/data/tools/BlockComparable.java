package me.clcondorcet.itemsorter.data.tools;

import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Location;

/**
 * @author clcondorcet
 */
public interface BlockComparable {

    boolean isSameBlock(FutureLocation other);
    boolean isSameBlock(Location other);
}
