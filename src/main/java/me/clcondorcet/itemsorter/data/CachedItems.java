package me.clcondorcet.itemsorter.data;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.config.Messages;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * @author clcondorcet
 */
public class CachedItems {

    private ItemStack prevE;
    private ItemStack prevD;
    private ItemStack nextE;
    private ItemStack nextD;
    private ItemStack plus;
    private ItemStack add;
    private ItemStack back;

    public CachedItems(){
        prevE = prevD = nextE = nextD = plus = add = back = null;
    }

    public ItemStack getPrevE() {
        if(prevE == null){
            prevE = new ItemStack(Material.SLIME_BALL);
            ItemMeta meta = prevE.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_prevEnable);
            prevE.setItemMeta(meta);
        }
        return prevE;
    }

    public ItemStack getPrevD() {
        if(prevD == null){
            prevD = new ItemStack(ItemSorter.versionHandler.getSnowBallMat());
            ItemMeta meta = prevD.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_prevDisable);
            prevD.setItemMeta(meta);
        }
        return prevD;
    }

    public ItemStack getNextE() {
        if(nextE == null){
            nextE = new ItemStack(Material.SLIME_BALL);
            ItemMeta meta = nextE.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_nextEnable);
            nextE.setItemMeta(meta);
        }
        return nextE;
    }

    public ItemStack getNextD() {
        if(nextD == null){
            nextD = new ItemStack(ItemSorter.versionHandler.getSnowBallMat());
            ItemMeta meta = nextD.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_nextDisable);
            nextD.setItemMeta(meta);
        }
        return nextD;
    }

    public ItemStack getPlus() {
        if(plus == null){
            plus = new ItemStack(ItemSorter.versionHandler.getFireworkBallMat());
            ItemMeta meta = plus.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_AddTrustName);
            plus.setItemMeta(meta);
        }
        return plus;
    }

    public ItemStack getAdd() {
        if(add == null){
            add = new ItemStack(ItemSorter.versionHandler.getFireworkBallMat());
            ItemMeta meta = add.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_AddFilterName);
            ArrayList<String> lore = new ArrayList<>();
            for(String st : ItemSorter.configManager.messages.inv_AddFilterLore){
                if(!st.equals("")){
                    lore.add(Messages.replaceColorCode(st));
                }
            }
            meta.setLore(lore);
            add.setItemMeta(meta);
        }
        return add;
    }

    public ItemStack getBack() {
        if(back == null){
            back = new ItemStack(Material.MAGMA_CREAM);
            ItemMeta meta = back.getItemMeta();
            meta.setDisplayName(ItemSorter.configManager.messages.inv_backName);
            back.setItemMeta(meta);
        }
        return back;
    }

}
