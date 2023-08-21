package me.clcondorcet.itemsorter.data;

import java.sql.SQLException;
import java.util.*;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.data.tools.BlockComparable;
import me.clcondorcet.itemsorter.data.tools.SignRefreshable;
import me.clcondorcet.itemsorter.database.schemas.SystemsTable;
import me.clcondorcet.itemsorter.processing.ItemTransferTick;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Pair;
import me.clcondorcet.itemsorter.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import static me.clcondorcet.itemsorter.listeners.EventsManager.autodeposits;
import static me.clcondorcet.itemsorter.listeners.EventsManager.autofilters;

/**
 * @author clcondorcet
 */
public class System implements SignRefreshable, BlockComparable {

	private String ownerName;
	private UUID ownerUUID;
	public boolean loaded = false;
	public int systemID;
	public final String name;
	public final FutureLocation baseLoc;
	public final FutureLocation sign;
	private final LinkedList<Trusted> trusted = new LinkedList<>();
	private final HashMap<Integer, Filter> filters = new HashMap<>();
	private final ArrayList<Deposit> deposits = new ArrayList<>();
	private final ArrayList<Filter> filtersLoading = new ArrayList<>();
	private final ArrayList<Deposit> depositsLoading = new ArrayList<>();

	public System(String name, FutureLocation baseLoc, FutureLocation signLoc, String ownerName, UUID ownerUUID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.name = name;
		this.baseLoc = baseLoc;
		this.sign = signLoc;
		this.ownerName = ownerName;
		this.ownerUUID = ownerUUID;
		String uuid_st = null;
		if (ownerUUID != null) uuid_st = ownerUUID.toString();
		this.systemID = SystemsTable.insertSystem(
				name,
				uuid_st,
				ownerName,
				baseLoc.getWorldName(),
				baseLoc.getBlockX(),
				baseLoc.getBlockY(),
				baseLoc.getBlockZ(),
				signLoc.getBlockX(),
				signLoc.getBlockY(),
				signLoc.getBlockZ()
		);
		DataManager.addSystem(this);
	}

	public System(String name, FutureLocation baseLoc, FutureLocation signLoc, String ownerName, UUID ownerUUID, AsyncAction.ItemSorterRunnable errorCallBack) {
		this.name = name;
		this.baseLoc = baseLoc;
		this.sign = signLoc;
		this.ownerName = ownerName;
		this.ownerUUID = ownerUUID;
		String uuid_st = null;
		if (ownerUUID != null) uuid_st = ownerUUID.toString();
		String finalUuid_st = uuid_st;
		DataManager.addLoadingSystem(this);
		ItemSorter.getInstance().asyncAction.async(() -> {
			if (!DataManager.getLoadingSystems().contains(this)) { // First check waiting for runnable to start
				return;
			}
			this.systemID = SystemsTable.insertSystem(
					name,
					finalUuid_st,
					ownerName,
					baseLoc.getWorldName(),
					baseLoc.getBlockX(),
					baseLoc.getBlockY(),
					baseLoc.getBlockZ(),
					signLoc.getBlockX(),
					signLoc.getBlockY(),
					signLoc.getBlockZ()
			);
			ItemSorter.getInstance().asyncAction.sync(() -> {
				if (DataManager.getLoadingSystems().contains(this)) { // Second check after sql and sync runnable start
					DataManager.addSystem(this);
					refreshSign(true);
				} else {
					DataManager.removeSystem(this);
					delete(false, false);
				}
			});
		}, () -> {
			DataManager.removeSystem(this);
			refreshSignAsync(false);
			errorCallBack.run();
		});
	}

	protected System(int systemID, String name, FutureLocation baseLoc, FutureLocation signLoc, String ownerName, UUID ownerUUID){
		this.name = name;
		this.baseLoc = baseLoc;
		this.sign = signLoc;
		this.ownerName = ownerName;
		this.ownerUUID = ownerUUID;
		this.systemID = systemID;
	}

	public boolean checkExistsInWorld() throws FutureLocation.WorldNotLoaded {
		return baseLoc.build().getBlock().getType() == Material.ENDER_CHEST && ItemSorter.versionHandler.isWallSign(sign.build().getBlock());
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwner(Player p) {
		setOwner(p.getName(), p.getUniqueId());
	}

	public void setOwner(String p, UUID uuid) {
		UUID ownerUUIDNow = null;
		if (ownerUUID != null) ownerUUIDNow = UUID.fromString(ownerUUID.toString());
		String ownerNameNow = this.ownerName;

		UUID newUUID = uuid;
		if (!Bukkit.getServer().getOnlineMode()) {
			newUUID = null;
		}
		if (!Objects.equals(ownerName, p) && !Objects.equals(newUUID, ownerUUID)) {
			ownerUUID = newUUID;
			ownerName = p;
			UUID finalNewUUID = newUUID;
			UUID finalOwnerUUIDNow = ownerUUIDNow;
			ItemSorter.getInstance().asyncAction.async(() -> {
				SystemsTable.setOwner(p, finalNewUUID, systemID);
				ItemSorter.getInstance().asyncAction.sync(() -> this.refreshAllSigns(true));
			}, () -> {
				ownerUUID = finalOwnerUUIDNow;
				ownerName = ownerNameNow;
				ItemSorter.getInstance().getLogger().warning("Owner UUID and name ('" + finalNewUUID + "', '" + p + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + finalOwnerUUIDNow + "', '" + ownerNameNow + "'");
			});
		} else if (!Objects.equals(ownerName, p) && Objects.equals(newUUID, ownerUUID)) {
			ownerName = p;
			ItemSorter.getInstance().asyncAction.async(() -> {
				SystemsTable.setOwnerName(p, systemID);
				ItemSorter.getInstance().asyncAction.sync(() -> this.refreshAllSigns(true));
			}, () -> {
				ownerName = ownerNameNow;
				ItemSorter.getInstance().getLogger().warning("Owner name ('" + p + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + ownerNameNow + "'");
			});
		} else if (!Objects.equals(newUUID, ownerUUID)) {
			this.ownerUUID = newUUID;
			String uuid_string = null;
			if (newUUID != null) {
				uuid_string = newUUID.toString();
			}
			UUID finalOwnerUUIDNow = ownerUUIDNow;
			String finalUuid_string = uuid_string;
			ItemSorter.getInstance().asyncAction.async(() -> {
				SystemsTable.setOwnerUUID(finalUuid_string, systemID);
			}, () -> {
				ownerUUID = finalOwnerUUIDNow;
				ItemSorter.getInstance().getLogger().warning("Owner UUID ('" + finalUuid_string + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + finalOwnerUUIDNow + "'");
			});
		}
	}

	protected boolean addTrusted(Trusted trust) {
		return trusted.add(trust);
	}

	protected boolean removeTrusted(Trusted trust) {
		return trusted.remove(trust);
	}

	public List<Trusted> getTrusted() {
		return new ArrayList<>(trusted);
	}

	protected void addFilter(Filter filter) {
		filtersLoading.remove(filter);
		filters.put(filter.filterID, filter);
		DataManager.filter.put(filter.loc, filter);
		DataManager.filter.put(filter.sign, filter);
	}

	protected void addLoadingFilter(Filter filter) {
		DataManager.filter.put(filter.loc, filter);
		DataManager.filter.put(filter.sign, filter);
		filtersLoading.add(filter);
	}

	public boolean removeLoadingFilter(Filter filter) {
		DataManager.filter.remove(filter.loc, filter);
		DataManager.filter.remove(filter.sign, filter);
		return filtersLoading.remove(filter);
	}

	protected void removeFilter(Filter filter) {
		filters.remove(filter.filterID);
		filtersLoading.remove(filter);
		DataManager.filter.remove(filter.loc, filter);
		DataManager.filter.remove(filter.sign, filter);
	}

	public List<Filter> getFilters() {
		return new ArrayList<>(filters.values());
	}

	public List<Filter> getLoadingFilters() {
		return new ArrayList<>(filtersLoading);
	}

	/**
	 * @return Collection of filters and loading filters
	 */
	public List<Filter> getAllFilters() {
		ArrayList<Filter> filters = new ArrayList<>(this.filters.values());
		filters.addAll(filtersLoading);
		return filters;
	}

	public Filter getFilterFromId(int filterID) {
		return filters.get(filterID);
	}

	protected boolean addDeposit(Deposit deposit) {
		depositsLoading.remove(deposit);
		DataManager.deposits.put(deposit.loc, deposit);
		DataManager.deposits.put(deposit.sign, deposit);
		return deposits.add(deposit);
	}

	protected boolean removeDeposit(Deposit deposit) {
		boolean rem = depositsLoading.remove(deposit);
		DataManager.deposits.remove(deposit.loc, deposit);
		DataManager.deposits.remove(deposit.sign, deposit);
		return deposits.remove(deposit) || rem;
	}

	protected boolean addLoadingDeposit(Deposit deposit) {
		DataManager.deposits.put(deposit.loc, deposit);
		DataManager.deposits.put(deposit.sign, deposit);
		return depositsLoading.add(deposit);
	}

	public boolean removeLoadingDeposit(Deposit deposit) {
		DataManager.deposits.remove(deposit.loc, deposit);
		DataManager.deposits.remove(deposit.sign, deposit);
		return depositsLoading.remove(deposit);
	}

	public List<Deposit> getDeposits() {
		return new ArrayList<>(deposits);
	}

	/**
	 * @return Collection of deposits and loading deposits
	 */
	public List<Deposit> getAllDeposits() {
		ArrayList<Deposit> deposits = new ArrayList<>(this.deposits);
		deposits.addAll(depositsLoading);
		return deposits;
	}

	public List<Deposit> getLoadingDeposits() {
		return new ArrayList<>(depositsLoading);
	}

	public void delete(boolean asyncSignUpdate, boolean disableRefreshSign){
		if (!DataManager.removeLoadingSystem(this)) {
			ItemSorter.getInstance().asyncAction.async(() -> {
				SystemsTable.deleteSystem(systemID);
			}, () -> {
				ItemSorter.getInstance().getLogger().warning("An error occurred. Normally it won't affect your world. If it did, please try contact clcondorcet (owner of the plugin).");
			});
		}
		if (!disableRefreshSign) {
			if (asyncSignUpdate) refreshSignAsync(false);
			else refreshSign(false);
		}
		DataManager.removeSystem(this);
		for(Trusted trusted1 : this.getTrusted()){
			trusted1.delete(false);
		}
		for(Filter filter : this.getFilters()){
			filter.delete(false, true, false);
		}
		for(Deposit deposit : this.getDeposits()){
			deposit.delete(false, true, false);
		}
		for(Player p : autodeposits.keySet()){
			if(autodeposits.get(p).equals(this)){
				autodeposits.remove(p);
			}
		}
		for(Player p : autofilters.keySet()){
			if(autofilters.get(p).equals(this)){
				autofilters.remove(p);
			}
		}
	}

	public boolean isOwner(Player p) {
		boolean uuidNull = ownerUUID == null;
		boolean sameUUID = p.getUniqueId().equals(ownerUUID);
		boolean onlineMode = Bukkit.getServer().getOnlineMode();
		boolean sameName = p.getName().equals(ownerName);
		if (!uuidNull) {
			if ((sameName && sameUUID) || (!sameName && !sameUUID)) {
				if (!onlineMode) setOwner(ownerName, null);
				return sameName;
			} else if (!sameName) {
				setOwner(p);
				return true;
			} else if (!onlineMode) {
				setOwner(ownerName, null);
				return true;
			}
		} else if (sameName) {
			setOwner(p);
			return true;
		}
		return false;
	}

	public boolean hasOwnerPermission(Player p) {
		return isOwner(p) || p.hasPermission("itemsorter.admin");
	}
	
	public boolean canAccess(Player p){
		if(isOwner(p)){
			return true;
		}
		for(Trusted trust : trusted){
			if(trust.isPlayer(p)){
				return true;
			}
		}
		return p.hasPermission("itemsorter.admin");
	}

	public boolean canAccessNoAdmin(Player p) {
		if(isOwner(p)){
			return true;
		}
		for(Trusted trust : trusted){
			if(trust.isPlayer(p)){
				return true;
			}
		}
		return false;
	}
	
	public static int getNewPriority(Collection<Filter> filters, Material mat){
		int max = 0;
		ArrayList<Integer> il = new ArrayList<>();
		for(Filter filter : filters){
			me.clcondorcet.itemsorter.data.Material matObj = filter.getMaterials().get(mat);
			int x = 0;
			if (matObj != null) {
				x = matObj.getPriority();
			}
			if(x > max){
				max = x;
			}
			if(x != 0){
				il.add(x);
			}
		}
		return getFirstUnusedValue(max, il);
	}
	
	public static int getNewTrashPriority(Collection<Filter> filters){
		int max = 0;
		ArrayList<Integer> il = new ArrayList<>();
		for(Filter filter : filters){
			if(filter.isTrash() && filter.getTrashPriority() > max){
				max = filter.getTrashPriority();
			}
			if(filter.isTrash() && filter.getTrashPriority() != 0){
				il.add(filter.getTrashPriority());
			}
		}
		return getFirstUnusedValue(max, il);
	}

	private static int getFirstUnusedValue(int max, ArrayList<Integer> il) {
		int i = 0;
		if(max != 0){
			for(i = 1; i <= max; i++){
				if(!il.contains(i)){
					return i;
				}
			}
			return i;
		}
		return i + 1;
	}

	public ArrayList<ItemStack> addItems (ArrayList<ItemStack> items) {
		ArrayList<ItemStack> returned = new ArrayList<>();
		for (ItemStack item : items) {
			ArrayList<ItemStack> enter = new ArrayList<>();
			enter.add(item);
			
			HashMap<Integer, Filter> filterMat = ItemTransferTick.getInstance().getFilterCached(this, item.getType());

			ArrayList<Integer> toRemove = new ArrayList<>();
			addInFilters(filterMat, item.getType(), enter, toRemove);
			if (enter.size() == 0) {
				continue;
			}

			HashMap<Integer, Filter> trash = ItemTransferTick.getInstance().getTrashCached(this);

			addInFilters(trash, item.getType(), enter, toRemove);
			if (!enter.isEmpty()) {
				returned.addAll(enter);
			}
		}
		return returned;
	}

	private void addInFilters(HashMap<Integer, Filter> filters, Material mat, ArrayList<ItemStack> enter, ArrayList<Integer> toRemove) {
		for (Filter filter : filters.values()) {
			if (DataManager.cachedFullFilters.containsKey(new Pair<>(filter.filterID, mat))) continue;
			try {
				Block block = filter.loc.build().getBlock();
				ArrayList<ItemStack> resultcache = new ArrayList<>();
				Inventory containerInv;
				try {
					containerInv = ((InventoryHolder) block.getState()).getInventory();
				} catch (Exception ex) {
					if (!Utilities.isContainer(block.getType()) || !ItemSorter.versionHandler.isWallSign(filter.sign.build().getBlock())) {
						toRemove.add(filter.filterID);
					}
					continue;
				}
				for (ItemStack itemToEnter : enter) {
					HashMap<Integer, ItemStack> result = containerInv.addItem(itemToEnter);
					if (!result.isEmpty()) {
						DataManager.cachedFullFilters.put(new Pair<>(filter.filterID, itemToEnter.getType()), filter);
					}
					resultcache.addAll(result.values());
				}
				enter.clear();
				enter.addAll(resultcache);
				if (enter.isEmpty()) break;
			} catch (FutureLocation.WorldNotLoaded ignored) {}
		}
		if (!toRemove.isEmpty()) {
			for (Integer i : toRemove) {
				Filter fil = filters.get(i);
				this.filters.remove(fil.filterID);
				filters.remove(i);
				fil.delete(true, true, true);
			}
		}
		toRemove.clear();
	}

	public HashMap<Integer, Filter> getfilters(System sys, Material mat){
		HashMap<Integer, Filter> filters = new HashMap<>();
		for(Filter filter : sys.filters.values()){
			if(filter.getMaterials().containsKey(mat)){
				filters.put(filter.getMaterials().get(mat).getPriority(), filter);
			}
		}
		return filters;
	}
	
	public HashMap<Integer, Filter> getfilters(System sys){
		HashMap<Integer, Filter> filters = new HashMap<>();
		for(Filter filter : sys.filters.values()){
			if(filter.isTrash()){
				filters.put(filter.getTrashPriority(), filter);
			}
		}
		return filters;
	}

	public Filter getFilterWithBlock(Block block){
		for(Filter fil : this.filters.values()){
			if(fil.isSameBlock(block.getLocation())){
				return fil;
			}
		}
		for(Filter fil : this.filtersLoading){
			if(fil.isSameBlock(block.getLocation())){
				return fil;
			}
		}
		return null;
	}

	public Deposit getDepositWithBlock(Block block){
		for(Deposit deposit : this.deposits){
			if(deposit.isSameBlock(block.getLocation())){
				return deposit;
			}
		}
		for(Deposit deposit : this.depositsLoading){
			if(deposit.isSameBlock(block.getLocation())){
				return deposit;
			}
		}
		return null;
	}

	public void refreshAllSigns(boolean enable) {
		LinkedList<SignRefreshable> srs = new LinkedList<>();
		srs.add(this);
		srs.addAll(getFilters());
		srs.addAll(getDeposits());
		refreshTask(srs, enable);
	}

	private void refreshTask(LinkedList<SignRefreshable> objects, boolean enable) {
		Bukkit.getScheduler().runTaskLater(ItemSorter.getInstance(), () -> {
			int size = objects.size();
			for (int i = 0; i < size && i < 10; i++) {
				SignRefreshable sr = objects.get(0);
				sr.refreshSign(enable);
				objects.remove(sr);
			}
			if (!objects.isEmpty()) {
				refreshTask(objects, enable);
			}
		}, 1);
	}

	@Override
	public void refreshSign(boolean enable) {
		try {
			Sign sign = (Sign) this.sign.build().getBlock().getState();
			if (enable) {
				sign.setLine(0, ItemSorter.configManager.messages.sign_prefix);
				sign.setLine(1, "§b" + name);
				sign.setLine(2, ItemSorter.configManager.messages.sign_base);
				sign.setLine(3, "§7(" + ownerName + ")");
			} else {
				sign.setLine(0, "§4" + ItemSorter.configManager.messages.sign_prefix.replaceAll("§.", ""));
				sign.setLine(1, "§4" + name);
				sign.setLine(2, "§4§m" + ItemSorter.configManager.messages.sign_base.replaceAll("§.", ""));
				sign.setLine(3, "§4(" + ownerName + ")");
			}
			sign.update();
		} catch (Exception ignored) {}
	}

	@Override
	public boolean isSameBlock(FutureLocation other) {
		return baseLoc.sameBlock(other) || sign.sameBlock(other);
	}

	@Override
	public boolean isSameBlock(Location other) {
		return baseLoc.sameBlock(other) || sign.sameBlock(other);
	}
}
