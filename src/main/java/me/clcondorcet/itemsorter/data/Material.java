package me.clcondorcet.itemsorter.data;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.database.schemas.MaterialsTable;
import me.clcondorcet.itemsorter.database.schemas.SystemsTable;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class Material {

    public final Filter filter;
    private int priority;
    public final org.bukkit.Material material;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority, AsyncAction.ItemSorterRunnable errorCallBack) {
        int oldPriority = this.priority;
        this.priority = priority;
        ItemSorter.getInstance().asyncAction.async(() -> {
            MaterialsTable.updatePriority(filter.sys.systemID, filter.filterID, material.name(), priority);
        }, () -> {
            this.priority = oldPriority;
            errorCallBack.run();
        });
    }

    public Material(Filter filter, org.bukkit.Material material, int priority, AsyncAction.ItemSorterRunnable errorCallBack) {
        this(filter, material, priority, true, errorCallBack);
    }

    protected Material(Filter filter, org.bukkit.Material material, int priority, boolean dbUpdate, AsyncAction.ItemSorterRunnable errorCallBack) {
        this.filter = filter;
        this.material = material;
        this.priority = priority;
        if (dbUpdate) {
            ItemSorter.getInstance().asyncAction.async(() -> {
                MaterialsTable.insertMaterial(filter.sys.systemID, filter.filterID, material.name(), priority);
            }, errorCallBack);
        }
        filter.addMaterial(this, errorCallBack);
    }

    protected Material(Filter filter, org.bukkit.Material material, int priority, boolean dbUpdate) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.filter = filter;
        this.material = material;
        this.priority = priority;
        if (dbUpdate) {
            MaterialsTable.insertMaterial(filter.sys.systemID, filter.filterID, material.name(), priority);
        }
        filter.addMaterial__Errors(this);
    }

    public void delete(boolean dbUpdate, AsyncAction.ItemSorterRunnable errorCallBack) {
        filter.removeMaterial(this, errorCallBack);
        if (dbUpdate) {
            ItemSorter.getInstance().asyncAction.async(() -> {
                MaterialsTable.deleteMaterial(filter.sys.systemID, filter.filterID, material.name());
            }, () -> {
                filter.addMaterial(this, () -> {
                    ItemSorter.getInstance().getLogger().severe("Cannot revert action! The material will be deleted from the filter at this location: " + filter.loc.toString() + " from this base '" + filter.sys.name + "'");
                });
                errorCallBack.run();
            });
        }
    }
}
