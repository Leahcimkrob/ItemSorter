package me.clcondorcet.itemsorter.listeners;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.config.Messages;
import me.clcondorcet.itemsorter.data.*;
import me.clcondorcet.itemsorter.data.System;
import me.clcondorcet.itemsorter.dependencies.AdvancedChestsDependency;
import me.clcondorcet.itemsorter.processing.ItemTransferTick;
import me.clcondorcet.itemsorter.purchase.SorterPurchaseGui;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

import static me.clcondorcet.itemsorter.data.DataManager.*;

/**
 * @author clcondorcet
 */
public class GuiEvents implements Listener {

    @EventHandler
    public void onClickInventory(InventoryClickEvent e){
        if (SorterPurchaseGui.isPurchaseInventory(e.getInventory())) {
            SorterPurchaseGui.handleClick(e);
            return;
        }
        AsyncAction.ItemSorterRunnable errorHandle = () -> {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
        };
        if (inFilter.containsKey((Player) e.getWhoClicked()) || inSystem.containsKey((Player) e.getWhoClicked())) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (inFilter.containsKey(p)) {
                try {
                    InFilterObject ifo = inFilter.get(p);
                    if (!e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                        try {
                            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                                if (e.getCurrentItem().equals(cachedItems.getNextE())) {
                                    ifo.page++;
                                } else if (e.getCurrentItem().equals(cachedItems.getPrevE())) {
                                    ifo.page--;
                                } else if (!e.getCurrentItem().equals(cachedItems.getAdd())) {
                                    switch (e.getClick()) {
                                        case DROP:
                                            if (!ifo.filter.isTrash())
                                                ifo.filter.getMaterials().get(e.getCurrentItem().getType()).delete(true, errorHandle);
                                            break;
                                        case RIGHT:
                                            if (ifo.filter.isTrash()) {
                                                ifo.filter.setTrashPriority(Math.max(ifo.filter.getTrashPriority()-1, 1), errorHandle);
                                            } else {
                                                me.clcondorcet.itemsorter.data.Material mat = ifo.filter.getMaterials().get(e.getCurrentItem().getType());
                                                mat.setPriority(Math.max(mat.getPriority()-1, 1), errorHandle);
                                            }
                                            break;
                                        case SHIFT_RIGHT:
                                            if (ifo.filter.isTrash()) {
                                                ifo.filter.setTrashPriority(Math.max(ifo.filter.getTrashPriority()-5, 1), errorHandle);
                                            } else {
                                                me.clcondorcet.itemsorter.data.Material mat = ifo.filter.getMaterials().get(e.getCurrentItem().getType());
                                                mat.setPriority(Math.max(mat.getPriority()-5, 1), errorHandle);
                                            }
                                            break;
                                        case LEFT:
                                            if (ifo.filter.isTrash()) {
                                                ifo.filter.setTrashPriority(Math.min(ifo.filter.getTrashPriority()+1, Integer.MAX_VALUE), errorHandle);
                                            } else {
                                                me.clcondorcet.itemsorter.data.Material mat = ifo.filter.getMaterials().get(e.getCurrentItem().getType());
                                                mat.setPriority(Math.min(mat.getPriority()+1, Integer.MAX_VALUE), errorHandle);
                                            }
                                            break;
                                        case SHIFT_LEFT:
                                            if (ifo.filter.isTrash()) {
                                                ifo.filter.setTrashPriority(Math.min(ifo.filter.getTrashPriority()+5, Integer.MAX_VALUE), errorHandle);
                                            } else {
                                                me.clcondorcet.itemsorter.data.Material mat = ifo.filter.getMaterials().get(e.getCurrentItem().getType());
                                                mat.setPriority(Math.min(mat.getPriority()+5, Integer.MAX_VALUE), errorHandle);
                                            }
                                            break;
                                    }
                                }
                                refreshFilterInv(p, p.getOpenInventory().getTopInventory(), ifo.filter, ifo.page);
                            }
                        } catch (Exception ignored) {
                            e.getWhoClicked().closeInventory();
                            e.getWhoClicked().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                        }
                    } else {
                        try {
                            if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)){
                                Material mat = e.getCurrentItem().getType();
                                if (!(ifo.filter.getMaterials().containsKey(mat))) {
                                    new me.clcondorcet.itemsorter.data.Material(ifo.filter, mat, System.getNewPriority(ifo.sys.getAllFilters(), mat), errorHandle);
                                    refreshFilterInv(p, p.getOpenInventory().getTopInventory(), ifo.filter, ifo.page);
                                } else {
                                    p.sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_alreadyExistFilter);
                                }
                            }
                        } catch (Exception ignored) {
                            e.getWhoClicked().closeInventory();
                            e.getWhoClicked().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                        }
                    }
                } catch(Exception ignored) {}
            } else {
                /*
                 * TODO OMG CHANGE THIS SHIT
                 */
                try {
                    if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                        return;
                    }
                    if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) {
                        return;
                    }
                    InSystemObject iso = inSystem.get(p);
                    if (e.getCurrentItem().equals(cachedItems.getNextE())) {
                        iso.page++;
                    } else if(e.getCurrentItem().equals(cachedItems.getPrevE())) {
                        iso.page--;
                    } else if (e.getCurrentItem().equals(cachedItems.getPlus()) || e.getCurrentItem().equals(cachedItems.getBack())) {
                        iso.page = 1;
                        iso.inMainMenu = !iso.inMainMenu;
                    } else if (iso.inMainMenu) {
                        if (e.getClick().equals(ClickType.SHIFT_RIGHT) && e.getSlot() + (iso.page - 1) * 27 < iso.sys.getTrusted().size()) {
                            iso.sys.getTrusted().get(e.getSlot() + (iso.page - 1) * 27).delete(true);
                        }
                    } else {
                        if (e.getCurrentItem().getType() == ItemSorter.versionHandler.getPlayerHeadMat()) {
                            try { // TODO Is there a better way ? Because this is shit ...
                                String name = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§e", "");
                                Player trustP = Bukkit.getPlayer(name);
                                assert trustP != null;
                                new Trusted(iso.sys, trustP.getName(), trustP.getUniqueId(), () -> {
                                    e.getWhoClicked().closeInventory();
                                    e.getWhoClicked().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                                });
                                iso.page = 1;
                                iso.inMainMenu = true;
                            } catch (Exception err) {
                                e.getWhoClicked().closeInventory();
                                e.getWhoClicked().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_error);
                            }
                        }
                    }
                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), iso.sys, iso.page, iso.inMainMenu);
                } catch (Exception ignored) {}
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e){
        try {
            if (!e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.ENDER_CHEST || ItemSorter.versionHandler.isWallSign(e.getClickedBlock()))) {
                Block click = e.getClickedBlock();
                FutureLocation clickLoc = new FutureLocation(click.getLocation());

                System sys = DataManager.bases.get(clickLoc);
                if (sys != null) {
                    e.setCancelled(true);
                    sys.refreshSignAsync(true);
                    if (sys.hasOwnerPermission(e.getPlayer())) {
                        Inventory inv = Bukkit.createInventory(null, 36, ItemSorter.configManager.messages.inv_trustName + sys.name);
                        inSystem.put(e.getPlayer(), new InSystemObject(sys, true, 1));
                        e.getPlayer().openInventory(inv);
                        refreshTrustInv(e.getPlayer(), inv, sys, 1, true);
                    } else {
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_onlyOwnerTrust);
                    }
                    return;
                }

                Filter filter = DataManager.filter.get(clickLoc);
                if (filter != null) {
                    sys = filter.sys;
                    e.setCancelled(true);
                    filter.refreshSignAsync(true);
                    for (InFilterObject data : inFilter.values()) {
                        if (data.filter.equals(filter)) {
                            e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_filterInUse);
                            return;
                        }
                    }
                    if (sys.canAccess(e.getPlayer())) {
                        Inventory inv = Bukkit.createInventory(null, 36, ItemSorter.configManager.messages.inv_filterName + sys.name);
                        inFilter.put(e.getPlayer(), new InFilterObject(filter, sys, 1));
                        e.getPlayer().openInventory(inv);
                        refreshFilterInv(e.getPlayer(), inv, filter, 1);
                    } else {
                        e.getPlayer().sendMessage(ItemSorter.prefix + ItemSorter.configManager.messages.msg_filterNeedTrust);
                    }
                    return;
                }

                Deposit deposit = DataManager.deposits.get(clickLoc);
                if (deposit != null) {
                    deposit.refreshSignAsync(true);
                }
            }
        } catch (Exception ignored) {}
    }

    @EventHandler
    public void onInventoryClosed(final InventoryCloseEvent e){
        if(inFilter.containsKey((Player)e.getPlayer())){
            inFilter.remove((Player)e.getPlayer());
        }else if(inSystem.containsKey((Player)e.getPlayer())){
            inSystem.remove((Player)e.getPlayer());
        }else {
            try {
                if (AdvancedChestsDependency.isRealInventoryCloseEvent(e)) ItemTransferTick.getInstance().transfer(e.getInventory());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        Bukkit.getScheduler().runTask(ItemSorter.getInstance(), () -> ((Player) e.getPlayer()).updateInventory());
    }

    public void refreshTrustInv(Player p, Inventory inv, System sys, int page, boolean isMain){
        if(isMain){
            inv.clear();
            int i = 0;
            int o = 0;
            for(Trusted trusted : sys.getTrusted()){
                if(i > 26 * page){
                    break;
                }
                if(i >= 27 * (page - 1) && i <= 26 * page){
                    ItemStack item = new ItemStack(ItemSorter.versionHandler.getPlayerHeadMat());
                    item = ItemSorter.versionHandler.skullItemModifVersion(item);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    meta.setOwner(trusted.name);
                    meta.setDisplayName("§e" + trusted.name);
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ItemSorter.configManager.messages.inv_trustRemove);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    inv.setItem(o, item);
                    o++;
                }
                i++;
            }
            if(page > 1){
                inv.setItem(27, cachedItems.getPrevE());
            }else{
                inv.setItem(27, cachedItems.getPrevD());
            }
            if(i > 26 * page){
                inv.setItem(35, cachedItems.getNextE());
            }else{
                inv.setItem(o, cachedItems.getPlus());
                inv.setItem(35, cachedItems.getNextD());
            }
            p.updateInventory();
        } else {
            inv.clear();
            int i = 0;
            int o = 0;
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!sys.canAccessNoAdmin(pl)){
                    if(i > 26 * page){
                        break;
                    }
                    if(i >= 27 * (page - 1) && i <= 26 * page){
                        ItemStack item = new ItemStack(ItemSorter.versionHandler.getPlayerHeadMat());
                        item = ItemSorter.versionHandler.skullItemModifVersion(item);
                        SkullMeta meta = (SkullMeta) item.getItemMeta();
                        meta.setOwner(pl.getName());
                        meta.setDisplayName("§e" + pl.getName());
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(ItemSorter.configManager.messages.inv_trustAdd);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        inv.setItem(o, item);
                        o++;
                    }
                    i++;
                }
            }
            if(page > 1){
                inv.setItem(27, cachedItems.getPrevE());
            }else{
                inv.setItem(27, cachedItems.getPrevD());
            }
            if(i > 26 * page){
                inv.setItem(35, cachedItems.getNextE());
            }else{
                inv.setItem(35, cachedItems.getNextD());
            }
            inv.setItem(31, cachedItems.getBack());
            p.updateInventory();
        }
    }

    public void refreshFilterInv(Player p, Inventory inv, Filter filter, int page){
        inv.clear();
        int i = 0;
        int o = 0;
        if(!filter.isTrash()){
            for(Material mat : filter.getMaterials().keySet()){
                if(i > (27 * page) - 1){
                    break;
                }
                if(i >= 27 * (page - 1) && i <= (27 * page) - 1){
                    ItemStack item = new ItemStack(mat);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ItemSorter.configManager.messages.inv_filterPriority + filter.getMaterials().get(mat).getPriority());
                    ArrayList<String> lore = new ArrayList<>();
                    for (String st : ItemSorter.configManager.messages.inv_filterLore) {
                        lore.add(Messages.replaceColorCode(st));
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    inv.setItem(o, item);
                    o++;
                }
                i++;
            }
        }else{
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_filterTrashPriority + filter.getTrashPriority());
            ArrayList<String> lore = new ArrayList<>();
            for (String st : ItemSorter.configManager.messages.inv_filterTrashLore) {
                lore.add(Messages.replaceColorCode(st));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(0, item);
            o++;
        }
        if(page > 1){
            inv.setItem(27, cachedItems.getPrevE());
        }else{
            inv.setItem(27, cachedItems.getPrevD());
        }
        if(i > (27 * page) - 1){
            inv.setItem(35, cachedItems.getNextE());
        }else{
            inv.setItem(o, cachedItems.getAdd());
            inv.setItem(35, cachedItems.getNextD());
        }
        p.updateInventory();
    }

}
