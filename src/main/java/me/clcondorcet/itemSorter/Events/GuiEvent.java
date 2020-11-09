package me.clcondorcet.itemSorter.Events;

import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Objects.Deposit;
import me.clcondorcet.itemSorter.Objects.Filter;
import me.clcondorcet.itemSorter.Objects.System;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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

public class GuiEvent  implements Listener {

    @EventHandler
    public void onClickInventory(InventoryClickEvent e){
        if(Main.inFilter.containsKey(e.getWhoClicked()) || Main.inSystem.containsKey(e.getWhoClicked())){
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if(Main.inFilter.containsKey(p)){
                try{
                    if(!e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
                        try{
                            if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)){
                                if(e.getCurrentItem().equals(Main.cachedItems.getNextE())){
                                    Main.inFilter.get(p)[2] = ((Integer)Main.inFilter.get(p)[2]) + 1;
                                    refreshFilterInv(p, p.getOpenInventory().getTopInventory(), (Filter)Main.inFilter.get(p)[0], (Integer)Main.inFilter.get(p)[2]);
                                }else if(e.getCurrentItem().equals(Main.cachedItems.getPrevE())){
                                    Main.inFilter.get(p)[2] = ((Integer)Main.inFilter.get(p)[2]) - 1;
                                    refreshFilterInv(p, p.getOpenInventory().getTopInventory(), (Filter)Main.inFilter.get(p)[0], (Integer)Main.inFilter.get(p)[2]);
                                }else if(!e.getCurrentItem().equals(Main.cachedItems.getAdd()) && !((Filter)Main.inFilter.get(p)[0]).isTrash && e.getClick().equals(ClickType.SHIFT_RIGHT)){
                                    ((Filter)Main.inFilter.get(p)[0]).materials.remove(e.getCurrentItem().getType());
                                    if(((Filter)Main.inFilter.get(p)[0]).materials.size() == 0){
                                        ((Filter)Main.inFilter.get(p)[0]).trashPriority = System.getNewPriority(((System)Main.inFilter.get(p)[1]).filters);
                                        ((Filter)Main.inFilter.get(p)[0]).isTrash = true;
                                    }
                                    ((System)Main.inFilter.get(p)[1]).save();
                                    refreshFilterInv(p, p.getOpenInventory().getTopInventory(), (Filter)Main.inFilter.get(p)[0], (Integer)Main.inFilter.get(p)[2]);
                                }
                            }
                        }catch(Exception ignored){}
                    }else{
                        try{
                            if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)){
                                Material mat = e.getCurrentItem().getType();
                                if(!((Filter)Main.inFilter.get(p)[0]).materials.containsKey(mat)){
                                    ((Filter)Main.inFilter.get(p)[0]).materials.put(mat, System.getNewPriority(((System)Main.inFilter.get(p)[1]).filters, mat));
                                    if(((Filter)Main.inFilter.get(p)[0]).materials.size() == 1){
                                        ((Filter)Main.inFilter.get(p)[0]).isTrash = false;
                                    }
                                    ((System)Main.inFilter.get(p)[1]).save();
                                    refreshFilterInv(p, p.getOpenInventory().getTopInventory(), (Filter)Main.inFilter.get(p)[0], (Integer)Main.inFilter.get(p)[2]);
                                }else{
                                    p.sendMessage(Main.prefix + Main.configManager.config.msg_alreadyExistFilter.replaceAll("&", "§"));
                                }
                            }
                        }catch(Exception ignored){}
                    }
                }catch(Exception ignored){}
            }else{
                try{
                    if(!e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
                        if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)){
                            if((boolean) Main.inSystem.get(p)[1]){
                                if(e.getCurrentItem().equals(Main.cachedItems.getNextE())){
                                    Main.inSystem.get(p)[2] = ((Integer)Main.inSystem.get(p)[2]) + 1;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }else if(e.getCurrentItem().equals(Main.cachedItems.getPrevE())){
                                    Main.inSystem.get(p)[2] = ((Integer)Main.inSystem.get(p)[2]) - 1;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }else if(e.getCurrentItem().equals(Main.cachedItems.getPlus())){
                                    Main.inSystem.get(p)[2] = 1;
                                    Main.inSystem.get(p)[1] = false;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }else if(e.getClick().equals(ClickType.SHIFT_RIGHT) && e.getSlot() < ((System)Main.inSystem.get(p)[0]).trusts.size()){
                                    ((System)Main.inSystem.get(p)[0]).trusts.remove(e.getSlot());
                                    ((System)Main.inSystem.get(p)[0]).save();
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }
                            }else{
                                if(e.getCurrentItem().equals(Main.cachedItems.getNextE())){
                                    Main.inSystem.get(p)[2] = ((Integer)Main.inSystem.get(p)[2]) + 1;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }else if(e.getCurrentItem().equals(Main.cachedItems.getPrevE())){
                                    Main.inSystem.get(p)[2] = ((Integer)Main.inSystem.get(p)[2]) - 1;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }else if(e.getCurrentItem().equals(Main.cachedItems.getBack())){
                                    Main.inSystem.get(p)[2] = 1;
                                    Main.inSystem.get(p)[1] = true;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }else if(e.getCurrentItem().getType() == Main.versionHandler.getPlayerHeadMat()){
                                    String name = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("§e", "");
                                    ((System)Main.inSystem.get(p)[0]).trusts.add(name);
                                    ((System)Main.inSystem.get(p)[0]).save();
                                    Main.inSystem.get(p)[2] = 1;
                                    Main.inSystem.get(p)[1] = true;
                                    refreshTrustInv(p, p.getOpenInventory().getTopInventory(), (System)Main.inSystem.get(p)[0], (Integer)Main.inSystem.get(p)[2], (boolean)Main.inSystem.get(p)[1]);
                                }
                            }
                        }
                    }
                }catch(Exception ignored){}
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e){
        try{
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.ENDER_CHEST || Main.versionHandler.isWallSign(e.getClickedBlock()))){
                Block click = e.getClickedBlock();
                String clickLoc = System.getLoc(click.getLocation());
                for(System sys : Main.bases){
                    if(System.getLoc(sys.baseLoc).equals(clickLoc) || System.getLoc(sys.sign).equals(clickLoc)){
                        e.setCancelled(true);
                        if ((System.getLoc(sys.sign).equals(clickLoc)) && !((Sign) click.getState()).getLine(0).equals(Main.configManager.config.sign_prefix.replace("&", "§"))) {
                            Sign sign = (Sign) click.getState();
                            sign.setLine(0, Main.configManager.config.sign_prefix.replace("&", "§"));
                            sign.setLine(1, "§b" + sys.name);
                            sign.setLine(2, "§b= BASE =");
                            sign.setLine(3, "§7(" + sys.owner + ")");
                            sign.update();
                        }
                        if(sys.owner.equals(e.getPlayer().getName()) || e.getPlayer().hasPermission("is.admin")){
                            Inventory inv = Bukkit.createInventory(null, 36, Main.configManager.config.inv_trustName.replaceAll("&", "§") + sys.name);
                            Object[] data = new Object[]{sys, true, 1};
                            Main.inSystem.put(e.getPlayer(), data);
                            e.getPlayer().openInventory(inv);
                            refreshTrustInv(e.getPlayer(), inv, sys, 1, true);
                        }else{
                            e.getPlayer().sendMessage(Main.prefix + Main.configManager.config.msg_onlyOwnerTrust.replaceAll("&", "§"));
                        }
                        return;
                    }
                    for(Filter filter : sys.filters){
                        if(System.getLoc(filter.sign).equals(clickLoc)){
                            if(!((Sign) click.getState()).getLine(0).equals(Main.configManager.config.sign_prefix.replace("&", "§"))){
                                Sign signe = (Sign) click.getState();
                                signe.setLine(0, Main.configManager.config.sign_prefix.replace("&", "§"));
                                signe.setLine(1, "§b" + sys.name);
                                signe.setLine(2, "§b- Filter -");
                                signe.setLine(3, "§7(" + sys.owner + ")");
                                signe.update();
                            }
                            for(Object[] data : Main.inFilter.values()){
                                if(data[0].equals(filter)){
                                    e.getPlayer().sendMessage(Main.prefix + Main.configManager.config.msg_filterInUse.replaceAll("&", "§"));
                                    return;
                                }
                            }
                            if(sys.isTrust(e.getPlayer())){
                                Inventory inv = Bukkit.createInventory(null, 36, Main.configManager.config.inv_filterName.replaceAll("&", "§") + sys.name);
                                Object[] data = new Object[]{filter, sys, 1};
                                Main.inFilter.put(e.getPlayer(), data);
                                e.getPlayer().openInventory(inv);
                                refreshFilterInv(e.getPlayer(), inv, filter, 1);
                            }else{
                                e.getPlayer().sendMessage(Main.prefix + Main.configManager.config.msg_filterNeedTrust.replaceAll("&", "§"));
                            }
                            return;
                        }
                    }
                    for(Deposit deposit : sys.deposits) {
                        if (System.getLoc(deposit.sign).equals(clickLoc)) {
                            if(!((Sign) click.getState()).getLine(0).equals(Main.configManager.config.sign_prefix.replace("&", "§"))){
                                Sign sign = (Sign) click.getState();
                                sign.setLine(0, Main.configManager.config.sign_prefix.replace("&", "§"));
                                sign.setLine(1, "§b" + sys.name);
                                sign.setLine(2, "§b- Deposit -");
                                sign.setLine(3, "§7(" + sys.owner + ")");
                                sign.update();
                            }
                        }
                    }
                }
            }
        }catch(Exception ignored){}
    }

    @EventHandler
    public void onInventoryClosed(final InventoryCloseEvent e){
        if(Main.inFilter.containsKey(e.getPlayer())){
            Main.inFilter.remove(e.getPlayer());
        }else if(Main.inSystem.containsKey(e.getPlayer())){
            Main.inSystem.remove(e.getPlayer());
        }else {
            Event.transfer(e.getInventory());
        }
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                ((Player) e.getPlayer()).updateInventory();
            }
        }, 0);
    }

    public void refreshTrustInv(Player p, Inventory inv, System sys, int page, boolean isMain){
        if(isMain){
            inv.clear();
            int i = 0;
            int o = 0;
            for(String st : sys.trusts){
                if(i > 26 * page){
                    break;
                }
                if(i >= 27 * (page - 1) && i <= 26 * page){
                    ItemStack item = new ItemStack(Main.versionHandler.getPlayerHeadMat());
                    item = Main.versionHandler.skullItemModifVersion(item);
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    meta.setOwner(st);
                    meta.setDisplayName("§e" + st);
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(Main.configManager.config.inv_trustRemove.replaceAll("&", "§"));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    inv.setItem(o, item);
                    o++;
                }
                i++;
            }
            if(page > 1){
                inv.setItem(27, Main.cachedItems.getPrevE());
            }else{
                inv.setItem(27, Main.cachedItems.getPrevD());
            }
            if(i > 26 * page){
                inv.setItem(35, Main.cachedItems.getNextE());
            }else{
                inv.setItem(o, Main.cachedItems.getPlus());
                inv.setItem(35, Main.cachedItems.getNextD());
            }
            p.updateInventory();
        }else{
            inv.clear();
            int i = 0;
            int o = 0;
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!sys.trusts.contains(pl.getName()) && !sys.owner.equals(pl.getName())){
                    if(i > 26 * page){
                        break;
                    }
                    if(i >= 27 * (page - 1) && i <= 26 * page){
                        ItemStack item = new ItemStack(Main.versionHandler.getPlayerHeadMat());
                        item = Main.versionHandler.skullItemModifVersion(item);
                        SkullMeta meta = (SkullMeta) item.getItemMeta();
                        meta.setOwner(pl.getName());
                        meta.setDisplayName("§e" + pl.getName());
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(Main.configManager.config.inv_trustAdd.replaceAll("&", "§"));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        inv.setItem(o, item);
                        o++;
                    }
                    i++;
                }
            }
            if(page > 1){
                inv.setItem(27, Main.cachedItems.getPrevE());
            }else{
                inv.setItem(27, Main.cachedItems.getPrevD());
            }
            if(i > 26 * page){
                inv.setItem(35, Main.cachedItems.getNextE());
            }else{
                inv.setItem(35, Main.cachedItems.getNextD());
            }
            inv.setItem(31, Main.cachedItems.getBack());
            p.updateInventory();
        }
    }

    public void refreshFilterInv(Player p, Inventory inv, Filter filter, int page){
        inv.clear();
        int i = 0;
        int o = 0;
        if(!filter.isTrash){
            for(Material mat : filter.materials.keySet()){
                if(i > 26 * page){
                    break;
                }
                if(i >= 27 * (page - 1) && i <= 26 * page){
                    ItemStack item = new ItemStack(mat);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(Main.configManager.config.inv_filterPriority.replaceAll("&", "§") + filter.materials.get(mat));
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(Main.configManager.config.inv_filterRemove.replaceAll("&", "§"));
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
            meta.setDisplayName(Main.configManager.config.inv_filterTrashPriority.replaceAll("&", "§") + filter.trashPriority);
            item.setItemMeta(meta);
            inv.setItem(0, item);
            o++;
        }
        if(page > 1){
            inv.setItem(27, Main.cachedItems.getPrevE());
        }else{
            inv.setItem(27, Main.cachedItems.getPrevD());
        }
        if(i > 26 * page){
            inv.setItem(35, Main.cachedItems.getNextE());
        }else{
            inv.setItem(o, Main.cachedItems.getAdd());
            inv.setItem(35, Main.cachedItems.getNextD());
        }
        p.updateInventory();
    }

}
