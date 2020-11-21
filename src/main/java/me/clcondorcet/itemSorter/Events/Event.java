package me.clcondorcet.itemSorter.Events;

import java.util.ArrayList;
import java.util.HashMap;

import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Objects.Deposit;
import me.clcondorcet.itemSorter.Objects.Filter;
import me.clcondorcet.itemSorter.Objects.System;
import me.clcondorcet.itemSorter.Utilities;
import me.clcondorcet.itemSorter.VersionChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Event implements Listener{
	/**
	 * 
	 * @author clcondorcet
	 */

	public static HashMap<Player, System> autofilters = new HashMap<>();
	public static HashMap<Player, System> autodeposits = new HashMap<>();

	@EventHandler
	public void onMoveItem(final InventoryMoveItemEvent e){
		new BukkitRunnable() {
			@Override
			public void run() {
				try{
					transfer(e.getDestination());
				}catch(Exception ignored){}
			}
		}.runTaskLater(Main.getInstance(), 2L);
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent e){
		if(Main.configManager.config.orderChestContent){
			Chest[] chests = new Chest[2];
			Object barrel = null;
			boolean isBarrel = false;
			if(e.getInventory().getHolder() instanceof DoubleChest){
				chests[0] = (Chest) ((DoubleChest) e.getInventory().getHolder()).getLeftSide();
				chests[1] = (Chest) ((DoubleChest) e.getInventory().getHolder()).getRightSide();
			}else if(e.getInventory().getHolder() instanceof Chest){
				chests = new Chest[1];
				chests[0] = ((Chest) e.getInventory().getHolder());
			}else if(Main.versionHandler.instanceOfBarel(e.getInventory().getHolder())){
				isBarrel = true;
				barrel = e.getInventory().getHolder();
			}else{
				return;
			}
			if(isBarrel){
				String blockLoc = System.getLoc(((Barrel)barrel).getLocation());
				reArrange((InventoryHolder) barrel, blockLoc);
			}else{
				for(Chest block : chests){
					String blockLoc = System.getLoc(block.getLocation());
					reArrange((InventoryHolder) block, blockLoc);
				}
			}
		}
	}
	
	public void reArrange(InventoryHolder invHold, String loc){
		for(System sys : Main.bases){
			for(Filter filter : sys.filters){
				if(System.getLoc(filter.loc).equals(loc)){
					HashMap<Material, ArrayList<ItemStack>> items = new HashMap<>();
					for(ItemStack item : invHold.getInventory().getContents()){
						try{
							if(item.getType() != Material.AIR){
								if(items.containsKey(item.getType())){
									items.get(item.getType()).add(item);
								}else{
									ArrayList<ItemStack> itemArray = new ArrayList<>();
									itemArray.add(item);
									items.put(item.getType(), itemArray);
								}
								invHold.getInventory().remove(item);
							}
						}catch(Exception ignored){}
					}
					for(Material mat : items.keySet()){
						for(ItemStack item : items.get(mat)){
							invHold.getInventory().addItem(item);
						}
					}
					return;
				}
			}
		}
	}
	
	public static void transfer(Inventory inv){
		try{
			if(inv.getHolder() != null){
				Chest chests[] = new Chest[2];
				Object barrel = null;
				boolean isBarrel = false;
				if(inv.getHolder() instanceof DoubleChest){
					chests[0] = (Chest) ((DoubleChest) inv.getHolder()).getLeftSide();
					chests[1] = (Chest) ((DoubleChest) inv.getHolder()).getRightSide();
				}else if(inv.getHolder() instanceof Chest){
					chests = new Chest[1];
					chests[0] = ((Chest) inv.getHolder());
				}else if(Main.versionHandler.instanceOfBarel(inv.getHolder())){
					isBarrel = true;
					barrel = inv.getHolder();
				}else{
					return;
				}
				if(isBarrel){
					String blockLoc = System.getLoc(((Barrel) barrel).getLocation());
					add((InventoryHolder) barrel, blockLoc);
				}else{
					for(Chest block : chests){
						String blockLoc = System.getLoc(block.getLocation());
						add((InventoryHolder) block, blockLoc);
					}
				}
			}
		}catch(NullPointerException ignored){
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
 	
	public static void add(InventoryHolder invHold, String loc){
		for(System sys : Main.bases){
			for(Deposit deposit : sys.deposits){
				if(System.getLoc(deposit.loc).equals(loc)){
					ArrayList<ItemStack> items = new ArrayList<>();
					for(ItemStack item : invHold.getInventory().getContents()){
						try{
							if(item.getType() != Material.AIR){
								items.add(item);
								invHold.getInventory().remove(item);
							}
						}catch(Exception ignored){}
					}
					ArrayList<ItemStack> result = sys.addItems(items);
					for(ItemStack itemToReEnter : result){
						invHold.getInventory().addItem(itemToReEnter);
					}
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
    private void onBlockBreak(BlockBreakEvent e) {
		if(!e.isCancelled()){
			if(Utilities.isBDF(e.getBlock().getType()) || Utilities.isSign(e.getBlock())){
				for(System sys : Main.bases){
					if(System.getLoc(sys.baseLoc).equals(System.getLoc(e.getBlock().getLocation())) || System.getLoc(sys.sign).equals(System.getLoc(e.getBlock().getLocation()))){
						if(sys.owner.equals(e.getPlayer().getName()) || e.getPlayer().hasPermission("is.admin")){
							Main.bases.remove(sys);
							sys.delete();
							e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_baseDeleted.replaceAll("&", "§"));
						}else{
							e.setCancelled(true);
							e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_needOwnerToBreak.replaceAll("&", "§"));
						}
						return;
					}
					for(Filter filter : sys.filters){
						if(System.getLoc(filter.loc).equals(System.getLoc(e.getBlock().getLocation())) || System.getLoc(filter.sign).equals(System.getLoc(e.getBlock().getLocation()))){
							if(sys.isTrust(e.getPlayer())){
								sys.filters.remove(filter);
								sys.save();
								e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_filterDeleted.replaceAll("&", "§"));
							}else{
								e.setCancelled(true);
								e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_needTrustBreakFilter.replaceAll("&", "§"));
							}
							return;
						}
					}
					for(Deposit deposit : sys.deposits){
						if(System.getLoc(deposit.loc).equals(System.getLoc(e.getBlock().getLocation())) || System.getLoc(deposit.sign).equals(System.getLoc(e.getBlock().getLocation()))){
							if(sys.isTrust(e.getPlayer())){
								sys.deposits.remove(deposit);
								sys.save();
								e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_depositDeleted.replaceAll("&", "§"));
							}else{
								e.setCancelled(true);
								e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_needTrustBreakDeposit.replaceAll("&", "§"));
							}
							return;
						}
					}
				}
			}
		}
    }
	
	@EventHandler
    private void onSignChange(final SignChangeEvent e) {
		if (autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer()) || e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_is + "]") || e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_isd + "]") || e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_isf + "]")) {
			Sign sign = (Sign) e.getBlock().getState();
			Block block = null;
			try {
				block = sign.getBlock().getRelative(Main.versionHandler.getBackBlock(sign));
			} catch (Exception ex) {
				return;
			}
			if (!(block != null && (Utilities.isBDF(block.getType())))) {
				e.getBlock().breakNaturally();
				if (e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_is + "]")) {
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_onEnderChest.replaceAll("&", "§"));
				} else {
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_onChestOrBarrel.replaceAll("&", "§"));
				}
				return;
			}
			boolean isSame = false;
			System source = null;
			if(autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer())){
				if(autofilters.containsKey(e.getPlayer())){
					source = autofilters.get(e.getPlayer());
				}else{
					source = autodeposits.get(e.getPlayer());
				}
			}else{
				for (System sys : Main.bases) {
					if (sys.name.equals(e.getLine(1).replaceAll(" ", ""))) {
						isSame = true;
						source = sys;
						break;
					}
				}
			}
			if (e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_is + "]")) {
				if (isSame || e.getLine(1).replaceAll(" ", "").equalsIgnoreCase("")) {
					e.getBlock().breakNaturally();
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_nameIncorrect.replaceAll("&", "§"));
					return;
				}
				if (block.getType() != Material.ENDER_CHEST) {
					e.getBlock().breakNaturally();
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_onEnderChest.replaceAll("&", "§"));
					return;
				}
				for (System sys : Main.bases) {
					if (System.getLoc(sys.baseLoc).equals(System.getLoc(block.getLocation()))) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_alreadyABase.replaceAll("&", "§"));
						return;
					}
				}
				if (!e.getPlayer().hasPermission("is.unlimitedBases")) {
					int count = 0;
					for (System sys : Main.bases) {
						if (sys.owner.equals(e.getPlayer().getName())) {
							count++;
						}
					}
					if (Utilities.getMaxBases(e.getPlayer()) <= count) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_tooMuchBase.replaceAll("&", "§"));
						return;
					}
				}
				final String name = e.getLine(1).replaceAll(" ", "");
				System newSystem = new System(name, System.getLoc(block.getLocation()), System.getLoc(sign.getLocation()), e.getPlayer().getName(), new ArrayList<String>(), new ArrayList<Filter>(), new ArrayList<Deposit>());
				Main.bases.add(newSystem);
				newSystem.save();
				final Location loc = sign.getLocation();
				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							Sign signe = (Sign) loc.getBlock().getState();
							signe.setLine(0, Main.configManager.messages.sign_prefix.replace("&", "§"));
							signe.setLine(1, "§b" + name);
							signe.setLine(2, "§b= BASE =");
							signe.setLine(3, "§7(" + e.getPlayer().getName() + ")");
							signe.update();
						} catch (Exception ignored) {
						}
					}
				}.runTaskLater(Main.getInstance(), 3L);
				e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_baseCreated.replaceAll("%name%", name).replaceAll("&", "§"));
				return;
			}
			if (autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer()) || e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_isd + "]") || e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_isf + "]")) {
				final String name = e.getLine(1).replaceAll(" ", "");
				if(!(autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer()))){
					if (!isSame || name.equalsIgnoreCase("")) {
						e.getBlock().breakNaturally();
						e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_nameDoesNotExist.replaceAll("&", "§"));
						return;
					}
				}
				if (!source.isTrust(e.getPlayer())) {
					e.getBlock().breakNaturally();
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_notTrust.replaceAll("&", "§"));
					return;
				}
				if (!Utilities.isDF(block.getType())) {
					e.getBlock().breakNaturally();
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_onChestOrBarrel.replaceAll("&", "§"));
					return;
				}
				for(System sys : Main.bases){
					Chest chests[] = new Chest[2];
					Object barrel = null;
					boolean isBarrel = false;
					Inventory inv = ((InventoryHolder) block.getState()).getInventory();
					if(inv.getHolder() instanceof DoubleChest){
						chests[0] = (Chest) ((DoubleChest) inv.getHolder()).getLeftSide();
						chests[1] = (Chest) ((DoubleChest) inv.getHolder()).getRightSide();
					}else if(inv.getHolder() instanceof Chest){
						chests = new Chest[1];
						chests[0] = ((Chest) inv.getHolder());
					}else if(Main.versionHandler.instanceOfBarel(inv.getHolder())){
						isBarrel = true;
						barrel = inv.getHolder();
					}
					ArrayList<Location> locs = new ArrayList<>();
					if(isBarrel){
						locs.add(block.getLocation());
					}else{
						for(Chest blocks : chests){
							locs.add(blocks.getLocation());
						}
					}
					for(Location loc : locs){
						if(sys.getDepositWithBlock(loc.getBlock()) != null){
							e.getBlock().breakNaturally();
							e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_alreadyDepositorFilter.replaceAll("&", "§"));
							return;
						}
						if(sys.getFilterWithBlock(loc.getBlock()) != null){
							e.getBlock().breakNaturally();
							e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_alreadyDepositorFilter.replaceAll("&", "§"));
							return;
						}
					}
				}
				int radiusH = Main.configManager.config.radius;
				int radiusV = Main.configManager.config.verticalRadius;
				int x = source.baseLoc.getBlockX() - block.getLocation().getBlockX();
				int y = source.baseLoc.getBlockY() - block.getLocation().getBlockY();
				int z = source.baseLoc.getBlockZ() - block.getLocation().getBlockZ();
				if (!(-radiusH <= x && radiusH >= x && -radiusH <= z && radiusH >= z && -radiusV <= y && radiusV >= y)) {
					e.getBlock().breakNaturally();
					e.getPlayer().sendMessage(Main.prefix + Main.configManager.messages.msg_notInRange.replaceAll("&", "§"));
					return;
				}
				if (autodeposits.containsKey(e.getPlayer()) || e.getLine(0).equalsIgnoreCase("[" + Main.configManager.messages.sign_prefix_input_isd + "]")) {
					final String owner = source.owner;
					final Location loc = sign.getLocation();
					System finalSource = source;
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								Sign signe = (Sign) loc.getBlock().getState();
								signe.setLine(0, Main.configManager.messages.sign_prefix.replace("&", "§"));
								signe.setLine(1, "§b" + finalSource.name);
								signe.setLine(2, "§b- Deposit -");
								signe.setLine(3, "§7(" + owner + ")");
								signe.update();
							} catch (Exception ignored) {
							}
						}
					}.runTaskLater(Main.getInstance(), 3L);
					source.deposits.add(new Deposit(block.getLocation(), sign.getLocation()));
					source.save();
				} else {
					final String owner = source.owner;
					final Location loc = sign.getLocation();
					System finalSource1 = source;
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								Sign signe = (Sign) loc.getBlock().getState();
								signe.setLine(0, Main.configManager.messages.sign_prefix.replace("&", "§"));
								signe.setLine(1, "§b" + finalSource1.name);
								signe.setLine(2, "§b- Filter -");
								signe.setLine(3, "§7(" + owner + ")");
								signe.update();
							} catch (Exception ignored) {
							}
						}
					}.runTaskLater(Main.getInstance(), 3L);
					source.filters.add(new Filter(block.getLocation(), sign.getLocation(), System.getNewPriority(source.filters)));
					source.save();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onPlaceEvent(BlockPlaceEvent e) {
		if(autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer())){
			if(!e.isCancelled()){
				if(Main.versionHandler.isWallSign(e.getBlockPlaced())){
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							e.getPlayer().closeInventory();
						}
					}, 0);
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(e.getPlayer().isOp()){
			if(Main.versionChecker.needUpdate){
				e.getPlayer().sendMessage(new String[]{"§6-------- §eItem§cSorter §6--------"
													 , "§e> §aItemSorter need an update !"
													 , "§eCurrent verion: §c" + VersionChecker.pluginVersion
													 , "§eThe new version is: §a" + Main.versionChecker.lastVersion
													 , "§6https://www.spigotmc.org/resources/itemsorter.85370/"
													 , "§6----------------------------"});

			}
		}
	}

	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		autofilters.remove(e.getPlayer());
		autodeposits.remove(e.getPlayer());
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent e){
		ArrayList<System> toDelete = new ArrayList<>();
		if(Main.notLoadedBases.containsKey(e.getWorld().getName())){
			FileConfiguration config = Main.configManager.getConfig("data.yml");
			for(String sys : Main.notLoadedBases.get(e.getWorld().getName())){
				try{
					System newSys = Main.loadSysFromFile(config, sys);
					if(newSys.baseLoc.getBlock().getType() != Material.ENDER_CHEST || !(Main.versionHandler.isWallSign(newSys.sign.getBlock()))){
						toDelete.add(newSys);
					}else{
						Main.bases.add(newSys);
						Main.log.info("The base " + sys + " is now loaded!");
					}
				}catch(Exception ex){
					Main.log.severe("This error may not occur please contact clcondorcet and sho him the this message and the error below: (#2)");
					ex.printStackTrace();
					Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
					return;
				}
			}
			Main.notLoadedBases.remove(e.getWorld().getName());
		}
		for(System sys : toDelete){
			Main.log.info("The base " + sys.name + " will be deleted because the base is not longer at the same location.");
			sys.delete();
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onExplosion(EntityExplodeEvent e){
		if(!e.isCancelled()){
			for(Block block : e.blockList()){
				if(Utilities.isSign(block) || Utilities.isBDF(block.getType())){
					for(System sys : Main.bases){
						if(sys.baseLoc.equals(block.getLocation()) || sys.sign.equals(block.getLocation())){
							Main.bases.remove(sys);
							sys.delete();
							return;
						}
						Filter fil = sys.getFilterWithBlock(block);
						if(fil != null){
							sys.filters.remove(fil);
							fil.delete();
						}else{
							Deposit depo = sys.getDepositWithBlock(block);
							if(depo != null){
								sys.deposits.remove(depo);
								depo.delete();
							}else{
								continue;
							}
						}
						sys.save();
						return;
					}
				}
			}
		}
	}
}
