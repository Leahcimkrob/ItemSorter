package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.config.Messages;
import me.clcondorcet.itemsorter.data.DataManager;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Utilities;
import me.clcondorcet.itemsorter.data.Deposit;
import me.clcondorcet.itemsorter.data.Filter;
import me.clcondorcet.itemsorter.data.System;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;

import static me.clcondorcet.itemsorter.listeners.EventsManager.autodeposits;
import static me.clcondorcet.itemsorter.listeners.EventsManager.autofilters;

/**
 * @author clcondorcet
 */
public class SignChangeEvent implements Listener {

    @EventHandler
    private void onSignChange(final org.bukkit.event.block.SignChangeEvent e) {
        // First, avoid modifying sign of already existing systems
        Sign sign = (Sign) e.getBlock().getState();
        FutureLocation signLoc = new FutureLocation(sign.getLocation());
        System SignSys = DataManager.bases.get(signLoc);
        if (SignSys != null) {
            SignSys.refreshSignAsync(true);
            e.setCancelled(true);
            return;
        }
        Filter SignFilter = DataManager.filter.get(signLoc);
        if (SignFilter != null) {
            SignFilter.refreshSignAsync(true);
            e.setCancelled(true);
            return;
        }
        Deposit SignDeposit = DataManager.deposits.get(signLoc);
        if (SignDeposit != null) {
            SignDeposit.refreshSignAsync(true);
            e.setCancelled(true);
            return;
        }

        // The real event
        String tag = e.getLine(0);
        String name = e.getLine(1);
        Messages messages = ItemSorter.configManager.messages;
        if (autofilters.containsKey(e.getPlayer())
            || autodeposits.containsKey(e.getPlayer())
            || (tag != null && (
                tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_is)
                    || tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_isd)
                    || tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_isf)
                )
            )
        ) {
            Block block;
            try {
                block = sign.getBlock().getRelative(ItemSorter.versionHandler.getBackBlock(sign));
            } catch (Exception ex) {
                return;
            }
            if (!Utilities.isContainerOrBaseBlock(block.getType())) {
                e.getBlock().breakNaturally();
                if (tag != null && tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_is)) {
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_onEnderChest);
                } else {
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_onChestOrBarrel);
                }
                return;
            }
            boolean isSame = false;
            System source = null;
            if (name != null) {
                name = name.replaceAll("\\s", "");
            }
            if(autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer())){
                if(autofilters.containsKey(e.getPlayer())){
                    source = autofilters.get(e.getPlayer());
                }else{
                    source = autodeposits.get(e.getPlayer());
                }
            }else{
                if (name != null) {
                    for (System sys : DataManager.getSystems()) {
                        if (sys.name.equals(name)) {
                            isSame = true;
                            source = sys;
                            break;
                        }
                    }
                }
            }
            if (tag != null && tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_is)) {
                if (isSame || name == null || name.isEmpty()) {
                    e.getBlock().breakNaturally();
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_nameIncorrect);
                    return;
                }
                if (block.getType() != Material.ENDER_CHEST) {
                    e.getBlock().breakNaturally();
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_onEnderChest);
                    return;
                }
                for (System sys : DataManager.getSystems()) {
                    if (sys.isSameBlock(block.getLocation())) {
                        e.getBlock().breakNaturally();
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_alreadyABase);
                        return;
                    }
                }
                if (!e.getPlayer().hasPermission("itemsorter.unlimitedBases")) {
                    int count = 0;
                    for (System sys : DataManager.getSystems()) {
                        if (sys.isOwner(e.getPlayer())) {
                            count++;
                        }
                    }
                    if (Utilities.getMaxBases(e.getPlayer()) <= count) {
                        e.getBlock().breakNaturally();
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_tooMuchBase);
                        return;
                    }
                }
                System newSystem = new System(name, new FutureLocation(block.getLocation()), new FutureLocation(sign.getLocation()), e.getPlayer().getName(), e.getPlayer().getUniqueId(), () -> {
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                });
                setLoading(e);
                e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_baseCreated.replaceAll("%name%", newSystem.name));
                return;
            }
            if (name != null && (
                autofilters.containsKey(e.getPlayer())
                    || autodeposits.containsKey(e.getPlayer())
                    || (tag != null && (
                        tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_isd)
                            || tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_isf)
                    ))
                )
            ) {
                if(!(autofilters.containsKey(e.getPlayer()) || autodeposits.containsKey(e.getPlayer()))){
                    if (!isSame || name.isEmpty()) {
                        e.getBlock().breakNaturally();
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_nameDoesNotExist);
                        return;
                    }
                }
                if (source == null) {
                    ItemSorter.getInstance().getLogger().severe("WTF Error ! Please send this to the author. #1");
                    return;
                }
                if (!source.canAccess(e.getPlayer())) {
                    e.getBlock().breakNaturally();
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_notTrust);
                    return;
                }
                if (!Utilities.isContainer(block.getType())) {
                    e.getBlock().breakNaturally();
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_onChestOrBarrel);
                    return;
                }
                for(System sys : DataManager.getSystems()){
                    Chest[] chests = new Chest[2];
                    boolean isBarrel = false;
                    Inventory inv = ((InventoryHolder) block.getState()).getInventory();
                    if(inv.getHolder() instanceof DoubleChest){
                        chests[0] = (Chest) ((DoubleChest) inv.getHolder()).getLeftSide();
                        chests[1] = (Chest) ((DoubleChest) inv.getHolder()).getRightSide();
                    }else if(inv.getHolder() instanceof Chest){
                        chests = new Chest[1];
                        chests[0] = ((Chest) inv.getHolder());
                    }else if(ItemSorter.versionHandler.instanceOfBarel(inv.getHolder())){
                        isBarrel = true;
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
                            e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_alreadyDepositorFilter);
                            return;
                        }
                        if(sys.getFilterWithBlock(loc.getBlock()) != null){
                            e.getBlock().breakNaturally();
                            e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_alreadyDepositorFilter);
                            return;
                        }
                    }
                }
                int radiusH = ItemSorter.configManager.config.radius;
                int radiusV = ItemSorter.configManager.config.verticalRadius;
                int x = source.baseLoc.getBlockX() - block.getLocation().getBlockX();
                int y = source.baseLoc.getBlockY() - block.getLocation().getBlockY();
                int z = source.baseLoc.getBlockZ() - block.getLocation().getBlockZ();
                if (!(-radiusH <= x && radiusH >= x && -radiusH <= z && radiusH >= z && -radiusV <= y && radiusV >= y)) {
                    e.getBlock().breakNaturally();
                    e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_notInRange);
                    return;
                }
                if (autodeposits.containsKey(e.getPlayer())
                        || (tag != null && tag.equalsIgnoreCase(messages.formatted_sign_prefix_input_isd))) {
                    new Deposit(source, new FutureLocation(block.getLocation()), new FutureLocation(sign.getLocation()), () -> {
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                    });
                    setLoading(e);
                } else {
                    new Filter(source, new FutureLocation(block.getLocation()), new FutureLocation(sign.getLocation()), true, System.getNewTrashPriority(source.getAllFilters()), () -> {
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                    });
                    setLoading(e);
                }
            }
        }
    }

    public void setLoading(final org.bukkit.event.block.SignChangeEvent e) {
        e.setLine(0, ItemSorter.configManager.messages.sign_prefix);
        e.setLine(1, ItemSorter.configManager.messages.sign_loading);
        e.setLine(2, "");
        e.setLine(3, "");
    }
}
