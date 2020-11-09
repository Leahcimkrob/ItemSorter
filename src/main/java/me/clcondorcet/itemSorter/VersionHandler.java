package me.clcondorcet.itemSorter;

import me.clcondorcet.itemSorter.Events.EventServerLoad;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class VersionHandler {

    public Integer[] version;

    VersionHandler(){
        this.version = getVersion(Main.version);
    }

    public boolean instanceOfBarel(Object obj){
        if(isVersionSupOrEqualThan("1_14")){
            return obj instanceof Barrel;
        }else{
            return false;
        }
    }

    public boolean isBarrelType(Material mat){
        if(isVersionSupOrEqualThan("1_14")){
            return mat.toString().equals("BARREL");
        }else{
            return false;
        }
    }

    public boolean isWallSign(Block obj){
        if(isVersionSupOrEqualThan("1_13")){
            return obj.getBlockData() instanceof WallSign;
        }else{
            return obj.getType().toString().equals("WALL_SIGN");
        }
    }

    public BlockFace getBackBlock(Sign sign){
        if(isVersionSupOrEqualThan("1_13")){
            return ((WallSign) sign.getBlockData()).getFacing().getOppositeFace();
        }else{
            switch(sign.getBlock().getState().getData().getData()){
                case 2:
                    return BlockFace.SOUTH;
                case 3:
                    return BlockFace.NORTH;
                case 4:
                    return BlockFace.EAST;
                case 5:
                    return BlockFace.WEST;
                default:
                    return null;
            }
        }
    }

    public Material getSnowBallMat(){
        if(isVersionSupOrEqualThan("1_13")){
            return Material.valueOf("SNOWBALL");
        }else{
            return Material.valueOf("SNOW_BALL");
        }
    }

    public Material getFireworkBallMat(){
        if(isVersionSupOrEqualThan("1_13")){
            return Material.valueOf("FIREWORK_STAR");
        }else{
            return Material.valueOf("FIREWORK_CHARGE");
        }
    }

    public Material getPlayerHeadMat(){
        if(isVersionSupOrEqualThan("1_13")){
            return Material.valueOf("PLAYER_HEAD");
        }else{
            return Material.valueOf("SKULL_ITEM");
        }
    }

    public ItemStack skullItemModifVersion(ItemStack item){
        if(isVersionSupOrEqualThan("1_13")){
            return item;
        }else{
            item.setDurability((short) 3);
            return item;
        }
    }

    public Object getOrCreateTag(Object item) {
        try{
            Object tags = item.getClass().getMethod("getTag").invoke(item);
            if (tags == null) {
                Class<?> nBTTagCompound = Class.forName("net.minecraft.server." + Main.version + ".NBTTagCompound");
                tags = nBTTagCompound.newInstance();
            }
            return tags;
        }catch(Exception ex){
            return null;
        }
    }

    public void newEvent(Plugin plug){
        if(isVersionSupOrEqualThan("1_13")){
            Bukkit.getServer().getPluginManager().registerEvents(new EventServerLoad(), plug);
        }
    }

    public boolean isVersionSupOrEqualThan(String version){
        Integer[] versionInt = getVersion(version);
        for(int i = 0; i < (Math.max(versionInt.length, this.version.length)); i++){
            if(versionInt.length == i){
                return this.version[i] == 0;
            }
            if(this.version.length == i){
                return true;
            }
            if(versionInt[i] > this.version[i]){
                return false;
            }
        }
        return true;
    }

    public static Integer[] getVersion(String version){
        String[] splitedVersion = version.replaceFirst("v", "").split("_");
        ArrayList<Integer> newVersion = new ArrayList<>();
        for(String values : splitedVersion){
            if(values.split("\\D").length > 1){
               break;
            }
            newVersion.add(Integer.parseInt(values));
        }
        Integer[] result = newVersion.toArray(new Integer[0]);
        return result;
    }

}
