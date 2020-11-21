package me.clcondorcet.itemSorter.Objects;

import java.util.ArrayList;
import java.util.HashMap;

import me.clcondorcet.itemSorter.Events.Event;
import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class System {

	public String name;
	public Location baseLoc;
	public Location sign;
	public String owner;
	public ArrayList<String> trusts;
	public ArrayList<Filter> filters;
	public ArrayList<Deposit> deposits;
	
	public System(String name, String baseLoc, String signLoc, String owner, ArrayList<String> trusts, ArrayList<Filter> filters, ArrayList<Deposit> deposits){
		this.name = name;
		this.baseLoc = getLoc(baseLoc);
		this.sign = getLoc(signLoc);
		this.owner = owner;
		this.trusts = trusts;
		this.filters = filters;
		this.deposits = deposits;
	}
	
	public static Location getLoc(String st){
		String[] sts = st.split(":");
		return new Location(Bukkit.getWorld(sts[0]), Double.parseDouble(sts[1]), Double.parseDouble(sts[2]), Double.parseDouble(sts[3]));
	}
	
	public static String getLoc(Location loc){
		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
	}
	
	public void save(){
		FileConfiguration config = Main.configManager.getConfig("data.yml");
		config.set("data." + this.name, null);
		config.set("data." + this.name + ".baseLoc", getLoc(this.baseLoc));
		config.set("data." + this.name + ".sign", getLoc(this.sign));
		config.set("data." + this.name + ".owner", owner);
		config.set("data." + this.name + ".trusts", trusts);
		int i = 0;
		for(Filter filter : filters){
			config.set("data." + this.name + ".filters." + String.valueOf(i) + ".isTrash", filter.isTrash);
			config.set("data." + this.name + ".filters." + String.valueOf(i) + ".TrashPriority", filter.trashPriority);
			config.set("data." + this.name + ".filters." + String.valueOf(i) + ".loc", getLoc(filter.loc));
			config.set("data." + this.name + ".filters." + String.valueOf(i) + ".sign", getLoc(filter.sign));
			ArrayList<String> sts = new ArrayList<>();
			for(Material mat : filter.materials.keySet()){
				sts.add(mat.toString() + ":" + filter.materials.get(mat));
			}
			config.set("data." + this.name + ".filters." + String.valueOf(i) + ".materials", sts);
			i++;
		}
		i = 0;
		for(Deposit deposit : deposits){
			config.set("data." + this.name + ".deposits." + String.valueOf(i) + ".loc", System.getLoc(deposit.loc));
			config.set("data." + this.name + ".deposits." + String.valueOf(i) + ".sign", System.getLoc(deposit.sign));
			i++;
		}
		Main.configManager.saveConfig("data.yml", Main.getInstance().getDataFolder());
	}
	
	public void delete(){
		for(Filter filter : this.filters){
			filter.delete();
		}
		for(Deposit deposit : this.deposits){
			deposit.delete();
		}
		for(Player p : Event.autodeposits.keySet()){
			if(Event.autodeposits.get(p).equals(this)){
				Event.autodeposits.remove(p);
			}
		}
		for(Player p : Event.autofilters.keySet()){
			if(Event.autofilters.get(p).equals(this)){
				Event.autofilters.remove(p);
			}
		}
		FileConfiguration config = Main.configManager.getConfig("data.yml");
		config.set("data." + this.name, null);
		Main.configManager.saveConfig("data.yml", Main.getInstance().getDataFolder());
	}
	
	public boolean isTrust(Player p){
		if(p.hasPermission("is.admin") || this.owner.equals(p.getName())){
			return true;
		}
		for(String st : trusts){
			if(st.equals(p.getName())){
				return true;
			}
		}
		return false;
	}
	
	public static int getNewPriority(ArrayList<Filter> filters, Material mat){
		int max = 0;
		ArrayList<Integer> il = new ArrayList<>();
		for(Filter filter : filters){
			int x = filter.materials.getOrDefault(mat, 0);
			if(x > max){
				max = x;
			}
			if(x != 0){
				il.add(x);
			}
		}
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
	
	public static int getNewPriority(ArrayList<Filter> filters){
		int max = 0;
		ArrayList<Integer> il = new ArrayList<>();
		for(Filter filter : filters){
			if(filter.isActive && filter.isTrash && filter.trashPriority > max){
				max = filter.trashPriority;
			}
			if(filter.isTrash && filter.trashPriority != 0){
				il.add(filter.trashPriority);
			}
		}
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
	
	public ArrayList<ItemStack> addItems(ArrayList<ItemStack> items){
		HashMap<Material, HashMap<Integer, Filter>> cache = new HashMap<>();
		HashMap<Integer, Filter> cacheTrash = new HashMap<>();
		boolean cacheSetup = false;
		ArrayList<ItemStack> returned = new ArrayList<>();
		for(ItemStack item : items){
			ArrayList<ItemStack> enter = new ArrayList<>();
			enter.add(item);
			
			HashMap<Integer, Filter> filterMat = new HashMap<>();
			if(!cache.containsKey(item.getType())){
				filterMat = getfilters(this, item.getType());
				cache.put(item.getType(), filterMat);
			}else{
				filterMat = cache.get(item.getType());
			}
			ArrayList<Integer> toRemove = new ArrayList<>();
			for(Integer i : filterMat.keySet()){
				Material mat = filterMat.get(i).loc.getBlock().getType();
				if(Utilities.isDF(mat) && Utilities.isSign(filterMat.get(i).sign.getBlock())){
					ArrayList<ItemStack> resultcache = new ArrayList<>();
					for(ItemStack itemToEnter : enter){
						HashMap<Integer, ItemStack> result = ((InventoryHolder) filterMat.get(i).loc.getBlock().getState()).getInventory().addItem(itemToEnter);
						resultcache.addAll(result.values());
					}
					enter.clear();
					enter.addAll(resultcache);
				}else{
					toRemove.add(i);
				}
			}
			if(!toRemove.isEmpty()){
				for(Integer i : toRemove){
					Filter fil = filterMat.get(i);
					this.filters.remove(fil);
					filterMat.remove(i);
					fil.delete();
				}
				this.save();
			}
			toRemove.clear();
			if(enter.size() == 0){
				continue;
			}
			if(!cacheSetup){
				cacheTrash = getfilters(this);
				cacheSetup = true;
			}
			
			for(Integer i : cacheTrash.keySet()){
				Material mat = cacheTrash.get(i).loc.getBlock().getType();
				if(Utilities.isDF(mat) && Utilities.isSign(cacheTrash.get(i).sign.getBlock())){
					ArrayList<ItemStack> resultcache = new ArrayList<>();
					for(ItemStack itemToEnter : enter){
						HashMap<Integer, ItemStack> result = ((InventoryHolder) cacheTrash.get(i).loc.getBlock().getState()).getInventory().addItem(itemToEnter);
						resultcache.addAll(result.values());
					}
					enter.clear();
					enter.addAll(resultcache);
				}else{
					toRemove.add(i);
				}
			}
			if(!toRemove.isEmpty()){
				for(Integer i : toRemove){
					Filter fil = cacheTrash.get(i);
					this.filters.remove(fil);
					cacheTrash.remove(i);
					fil.delete();
				}
				this.save();
			}
			toRemove.clear();
			if(!enter.isEmpty()){
				returned.addAll(enter);
			}
		}
		return returned;
	}

	public HashMap<Integer, Filter> getfilters(System sys, Material mat){
		HashMap<Integer, Filter> filters = new HashMap<>();
		for(Filter filter : sys.filters){
			if(filter.materials.containsKey(mat)){
				filters.put(filter.materials.get(mat), filter);
			}
		}
		return filters;
	}
	
	public HashMap<Integer, Filter> getfilters(System sys){
		HashMap<Integer, Filter> filters = new HashMap<>();
		for(Filter filter : sys.filters){
			if(filter.isTrash){
				filters.put(filter.trashPriority, filter);
			}
		}
		return filters;
	}

	public Filter getFilterWithBlock(Block block){
		for(Filter fil : this.filters){
			if(fil.loc.equals(block.getLocation()) || fil.sign.equals(block.getLocation())){
				return fil;
			}
		}
		return null;
	}

	public Deposit getDepositWithBlock(Block block){
		for(Deposit deposit : this.deposits){
			if(deposit.loc.equals(block.getLocation()) || deposit.sign.equals(block.getLocation())){
				return deposit;
			}
		}
		return null;
	}
}
