package me.clcondorcet.itemSorter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utilities {

    /*
    public static ItemStack setNbt(ItemStack item){
        ItemStack nbtItem = item;
        try{
            Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + Main.version + ".inventory.CraftItemStack");
            Object itemb = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(craftItemStackClass, nbtItem);
            Object tags = Main.versionHandler.getOrCreateTag(itemb);
            if(tags == null){
                Bukkit.getConsoleSender().sendMessage("Error on nbt's 2");
                return nbtItem;
            }
            tags.getClass().getMethod("setString", String.class, String.class).invoke(tags, "type", "menu");
            itemb.getClass().getMethod("setTag", tags.getClass()).invoke(itemb, tags);
            nbtItem = (ItemStack) craftItemStackClass.getMethod("asBukkitCopy", itemb.getClass()).invoke(craftItemStackClass, itemb);
        }catch (Exception e){
            Bukkit.getConsoleSender().sendMessage("Error on nbt's");
        }
        return nbtItem;
    }

    public boolean isNbtMenu(ItemStack item){
        try{
            Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + Main.version + ".inventory.CraftItemStack");
            Object itemB = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(craftItemStackClass, item);
            Object tags = itemB.getClass().getMethod("getOrCreateTag").invoke(itemB);
            return !((boolean)tags.getClass().getMethod("isEmpty").invoke(tags) && ((boolean) ((String)tags.getClass().getMethod("getString", String.class).invoke("type")).equals("menu")));
        }catch (Exception ex){
            return true;
        }
    }*/

    public static int getMaxBases(Player p){
        int max = Main.configManager.config.maxBases_default;
        for(String perm : Main.configManager.config.maxBases_others.keySet()){
            if(p.hasPermission(perm)) {
                int newMax = Main.configManager.config.maxBases_others.get(perm);
                if (max < newMax) {
                    max = newMax;
                }
            }
        }
        return max;
    }

    public static boolean isDF(Material mat){
        return mat.equals(Material.CHEST) || mat.equals(Material.TRAPPED_CHEST) || Main.versionHandler.isBarrelType(mat);
    }

    public static boolean isBDF(Material mat){
        return mat.equals(Material.CHEST) || mat.equals(Material.TRAPPED_CHEST) || Main.versionHandler.isBarrelType(mat) || mat.equals(Material.ENDER_CHEST);
    }

    public static boolean isSign(Block block){
        return Main.versionHandler.isWallSign(block);
    }

    public static List<String> searchforsimilarity(String regex, List<String> from){
        if(from.isEmpty()){
            return from;
        }
        if(regex.equals("") || regex.equals(" ") || regex == null){
            return from;
        }
        ArrayList<String> newlist = new ArrayList<String>();
        String[] regexsplit = regex.split("");
        for(String st : from){
            String[] fromsplit = st.split("");
            boolean same = true;
            for(int i = 0; i < regexsplit.length; i++){
                if(i < fromsplit.length && regexsplit[i].equalsIgnoreCase(fromsplit[i])){
                }else{
                    same = false;
                    i = regexsplit.length;
                }
            }
            if(same == true){
                newlist.add(st);
            }
        }
        return newlist;
    }

    public static <T> T[] sort(T[] list, compareSup compare){
        boolean isSorted = false;
        while(!isSorted){
            isSorted = true;
            for(int i = 0; i < list.length-1; i++){
                if(compare.compare(list[i], list[i+1])){
                    isSorted = false;
                    T temp = list[i];
                    list[i] = list[i+1];
                    list[i+1] = temp;
                }
            }
        }
        return list;
    }

    public interface compareSup{
        boolean compare(Object a, Object b);
    }
}
