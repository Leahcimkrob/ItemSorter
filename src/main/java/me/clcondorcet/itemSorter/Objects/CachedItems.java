package me.clcondorcet.itemSorter.Objects;

import me.clcondorcet.itemSorter.Main;
import me.clcondorcet.itemSorter.Utilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

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
            meta.setDisplayName(Main.configManager.messages.inv_prevEnable.replaceAll("&", "§"));
            prevE.setItemMeta(meta);
            prevE = Utilities.setNbt(prevE);
        }
        return prevE;
    }

    public ItemStack getPrevD() {
        if(prevD == null){
            prevD = new ItemStack(Main.versionHandler.getSnowBallMat());
            ItemMeta meta = prevD.getItemMeta();
            meta.setDisplayName(Main.configManager.messages.inv_prevDisable.replaceAll("&", "§"));
            prevD.setItemMeta(meta);
            prevD = Utilities.setNbt(prevD);
        }
        return prevD;
    }

    public ItemStack getNextE() {
        if(nextE == null){
            nextE = new ItemStack(Material.SLIME_BALL);
            ItemMeta meta = nextE.getItemMeta();
            meta.setDisplayName(Main.configManager.messages.inv_nextEnable.replaceAll("&", "§"));
            nextE.setItemMeta(meta);
            nextE = Utilities.setNbt(nextE);
        }
        return nextE;
    }

    public ItemStack getNextD() {
        if(nextD == null){
            nextD = new ItemStack(Main.versionHandler.getSnowBallMat());
            ItemMeta meta = nextD.getItemMeta();
            meta.setDisplayName(Main.configManager.messages.inv_nextDisable.replaceAll("&", "§"));
            nextD.setItemMeta(meta);
            nextD = Utilities.setNbt(nextD);
        }
        return nextD;
    }

    public ItemStack getPlus() {
        if(plus == null){
            plus = new ItemStack(Main.versionHandler.getFireworkBallMat());
            ItemMeta meta = plus.getItemMeta();
            meta.setDisplayName(Main.configManager.messages.inv_AddTrustName.replaceAll("&", "§"));
            plus.setItemMeta(meta);
            plus = Utilities.setNbt(plus);
        }
        return plus;
    }

    public ItemStack getAdd() {
        if(add == null){
            add = new ItemStack(Main.versionHandler.getFireworkBallMat());
            ItemMeta meta = add.getItemMeta();
            meta.setDisplayName(Main.configManager.messages.inv_AddFilterName.replaceAll("&", "§"));
            ArrayList<String> lore = new ArrayList<>();
            for(String st : Main.configManager.messages.inv_AddFilterLore){
                if(!st.equals("")){
                    lore.add(st.replaceAll("&", "§"));
                }
            }
            meta.setLore(lore);
            add.setItemMeta(meta);
            add = Utilities.setNbt(add);
        }
        return add;
    }

    public ItemStack getBack() {
        if(back == null){
            back = new ItemStack(Material.MAGMA_CREAM);
            ItemMeta meta = back.getItemMeta();
            meta.setDisplayName(Main.configManager.messages.inv_backName.replaceAll("&", "§"));
            back.setItemMeta(meta);
            back = Utilities.setNbt(back);
        }
        return back;
    }

}
