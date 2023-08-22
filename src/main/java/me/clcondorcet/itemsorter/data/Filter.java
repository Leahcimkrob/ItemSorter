package me.clcondorcet.itemsorter.data;

import java.sql.SQLException;
import java.util.HashMap;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.tools.BlockComparable;
import me.clcondorcet.itemsorter.data.tools.SignRefreshable;
import me.clcondorcet.itemsorter.database.schemas.FiltersTable;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.block.Sign;

/**
 * @author clcondorcet
 */
public class Filter implements SignRefreshable, BlockComparable {

	public final System sys;
	public int filterID;
	public final FutureLocation loc;
	public final FutureLocation sign;
	private final HashMap<org.bukkit.Material, me.clcondorcet.itemsorter.data.Material> materials = new HashMap<>();
	private boolean isTrash;
	private int trashPriority;

	public boolean isTrash() {
		return isTrash;
	}

	public void setIsTrash(boolean newValue, AsyncAction.ItemSorterRunnable errorCallBack) {
		if (newValue != isTrash) {
			isTrash = newValue;
			ItemSorter.getInstance().asyncAction.async(() -> {
				FiltersTable.updateIsTrash(filterID, newValue);
			}, () -> {
				isTrash = !newValue;
				errorCallBack.run();
			});
		}
	}

	public void setIsTrash__Errors(boolean newValue) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (newValue != isTrash) {
			isTrash = newValue;
			FiltersTable.updateIsTrash(filterID, newValue);
		}
	}

	public int getTrashPriority() {
		return trashPriority;
	}

	public void setTrashPriority(int value, AsyncAction.ItemSorterRunnable errorCallBack) {
		if (value != trashPriority) {
			int oldTrashPriority = trashPriority;
			trashPriority = value;
			ItemSorter.getInstance().asyncAction.async(() -> {
				FiltersTable.updateTrashPriority(filterID, value);
			}, () -> {
				trashPriority = oldTrashPriority;
				errorCallBack.run();
			});
		}
	}

	public void setTrashPriorityAndIsTrash(int trashPriority, boolean isTrash, AsyncAction.ItemSorterRunnable errorCallBack) {
		if (trashPriority != this.trashPriority && isTrash != this.isTrash) {
			int oldTrashPriority = this.trashPriority;
			this.trashPriority = trashPriority;
			this.isTrash = isTrash;
			ItemSorter.getInstance().asyncAction.async(() -> {
				FiltersTable.updateTrashPriorityAndIsTrash(filterID, trashPriority, isTrash);
			}, () -> {
				this.trashPriority = oldTrashPriority;
				this.isTrash = !isTrash;
				errorCallBack.run();
			});
		} else if (trashPriority != this.trashPriority) {
			int oldTrashPriority = this.trashPriority;
			this.trashPriority = trashPriority;
			ItemSorter.getInstance().asyncAction.async(() -> {
				FiltersTable.updateTrashPriority(filterID, trashPriority);
			}, () -> {
				this.trashPriority = oldTrashPriority;
				errorCallBack.run();
			});
		} else if (isTrash != this.isTrash) {
			this.isTrash = isTrash;
			ItemSorter.getInstance().asyncAction.async(() -> {
				FiltersTable.updateIsTrash(filterID, isTrash);
			}, () -> {
				this.isTrash = !isTrash;
				errorCallBack.run();
			});
		}
	}

	protected void addMaterial(Material mat, AsyncAction.ItemSorterRunnable errorCallBack) {
		materials.put(mat.material, mat);
		AsyncAction.ItemSorterRunnable errorCall = () -> {
			materials.remove(mat.material);
			errorCallBack.run();
		};
		setIsTrash(false, errorCall);
	}

	protected void addMaterial__Errors(Material mat) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		materials.put(mat.material, mat);
		setIsTrash__Errors(false);
	}

	protected void removeMaterial(Material mat, AsyncAction.ItemSorterRunnable errorCallBack) {
		materials.remove(mat.material);
		if (materials.size() == 0) {
			AsyncAction.ItemSorterRunnable errorCall = () -> {
				materials.put(mat.material, mat);
				errorCallBack.run();
			};
			setTrashPriorityAndIsTrash(System.getNewTrashPriority(sys.getAllFilters()), true, errorCall);
		}
	}

	public HashMap<org.bukkit.Material, Material> getMaterials() {
		return materials;
	}

	public Filter(System sys, FutureLocation loc, FutureLocation sign, boolean isTrash, int trashPriority) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.sys = sys;
		this.loc = loc;
		this.sign = sign;
		this.isTrash = isTrash;
		this.trashPriority = trashPriority;
		this.filterID = FiltersTable.insertFilter(
				sys.systemID,
				isTrash,
				trashPriority,
				loc.getWorldName(),
				loc.getBlockX(),
				loc.getBlockY(),
				loc.getBlockZ(),
				sign.getBlockX(),
				sign.getBlockY(),
				sign.getBlockZ()
		);
		sys.addFilter(this);
	}

	public Filter(System sys, FutureLocation loc, FutureLocation sign, boolean isTrash, int trashPriority, AsyncAction.ItemSorterRunnable errorCallBack) {
		this.sys = sys;
		this.loc = loc;
		this.sign = sign;
		this.isTrash = isTrash;
		this.trashPriority = trashPriority;
		sys.addLoadingFilter(this);
		ItemSorter.getInstance().asyncAction.async(() -> {
			if (!sys.getLoadingFilters().contains(this)) { // First check waiting for runnable to start
				return;
			}
			this.filterID = FiltersTable.insertFilter(
					sys.systemID,
					isTrash,
					trashPriority,
					loc.getWorldName(),
					loc.getBlockX(),
					loc.getBlockY(),
					loc.getBlockZ(),
					sign.getBlockX(),
					sign.getBlockY(),
					sign.getBlockZ()
			);
			ItemSorter.getInstance().asyncAction.sync(() -> {
				if (sys.getLoadingFilters().contains(this)) { // Second check after sql and sync runnable start
					sys.addFilter(this);
					refreshSign(true);
				} else {
					sys.removeFilter(this);
					delete(true, false, true);
				}
			});
		}, () -> {
			sys.removeFilter(this);
			refreshSignAsync(false);
			errorCallBack.run();
		});
	}
	
	protected Filter(int filterID, System sys, FutureLocation loc, FutureLocation sign, boolean isTrash, int trashPriority){
		this.filterID = filterID;
		this.sys = sys;
		this.loc = loc;
		this.sign = sign;
		this.isTrash = isTrash;
		this.trashPriority = trashPriority;
		sys.addFilter(this);
	}

	/*
	public int canFilter(org.bukkit.Material mat){
		if(!this.isActive){
			return -2;
		}
		if(isTrash){
			return trashPriority;
		}else{
			return materials.getOrDefault(mat, -1);
		}
	}
	 */

	public void delete(boolean dbUpdate, boolean asyncSignUpdate, boolean disableRefreshSign) {
		sys.removeFilter(this);
		if (dbUpdate && !sys.removeLoadingFilter(this)) {
			ItemSorter.getInstance().asyncAction.async(() -> {
				FiltersTable.deleteFilter(this.filterID);
			}, () -> {
				ItemSorter.getInstance().getLogger().warning("An error occurred. Normally it won't affect your world. If it did, please try contact clcondorcet (owner of the plugin).");
			});
		}
		if (!disableRefreshSign) {
			if (asyncSignUpdate) refreshSignAsync(false);
			else refreshSign(false);
		}
	}

	@Override
	public void refreshSign(boolean enable) {
		try{
			Sign sign = (Sign) this.sign.build().getBlock().getState();
			if (enable) {
				sign.setLine(0, ItemSorter.configManager.messages.sign_prefix);
				sign.setLine(1, "§b" + sys.name);
				sign.setLine(2, ItemSorter.configManager.messages.sign_filter);
				sign.setLine(3, "§7(" + sys.getOwnerName() + ")");
			} else {
				sign.setLine(0, "§4" + ItemSorter.configManager.messages.sign_prefix.replaceAll("§.", ""));
				sign.setLine(1, "§4" + sys.name);
				sign.setLine(2, "§4§m" + ItemSorter.configManager.messages.sign_filter.replaceAll("§.", ""));
				sign.setLine(3, "§4(" + sys.getOwnerName() + ")");
			}
			sign.update();
		}catch(Exception ignored){}
	}

	@Override
	public boolean isSameBlock(FutureLocation other) {
		return loc.sameBlock(other) || sign.sameBlock(other);
	}

	@Override
	public boolean isSameBlock(Location other) {
		return loc.sameBlock(other) || sign.sameBlock(other);
	}

	public boolean checkExistsInWorld() throws FutureLocation.WorldNotLoaded {
		return Utilities.isContainer(loc.build().getBlock().getType()) && ItemSorter.versionHandler.isWallSign(sign.build().getBlock());
	}
}
