package me.clcondorcet.itemsorter.multiversion;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.listeners.ServerLoadEvent;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author clcondorcet
 */
public class VersionHandler {

    public static Integer[] version;

    public VersionHandler(){
        version = getVersion(ItemSorter.version);
    }

    public Object getpacketSpawnEntityFallingBlock(Class packetSpawnEntityClass, Location loc, Integer id) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, InstantiationException {
        if(isVersionSupOrEqualThan("1_19")){
            Class<?> entityTypesClass = Class.forName("net.minecraft.world.entity.EntityTypes");

            Class<?> vec3DClass = Class.forName("net.minecraft.world.phys.Vec3D");
            Object vec3D = vec3DClass.getDeclaredConstructors()[0].newInstance(0.0d, 0.0d, 0.0d);
            Constructor<?> spawnEntityClassConstructor = packetSpawnEntityClass.getDeclaredConstructor(
                    int.class,
                    UUID.class,
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class,
                    entityTypesClass,
                    int.class,
                    vec3DClass,
                    double.class
            );
            Object packet = spawnEntityClassConstructor.newInstance(
                    id,
                    UUID.randomUUID(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    0.0f,
                    0.0f,
                    entityTypesClass.getDeclaredField(getEntityTypeFallingBlockField()).get(null),
                    getMaterialIdNBTQuartz(),
                    vec3D,
                    0.0d
            );
            return packet;
        } else if(isVersionSupOrEqualThan("1_18")){
            Class<?> entityTypesClass = Class.forName("net.minecraft.world.entity.EntityTypes");

            Class<?> vec3DClass = Class.forName("net.minecraft.world.phys.Vec3D");
            Object vec3D = vec3DClass.getDeclaredConstructor(double.class, double.class, double.class).newInstance(0.0d, 0.0d, 0.0d);
            Constructor<?> spawnEntityClassConstructor = packetSpawnEntityClass.getDeclaredConstructor(
                    int.class,
                    UUID.class,
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class,
                    entityTypesClass,
                    int.class,
                    vec3DClass
            );
            Object packet = spawnEntityClassConstructor.newInstance(
                    id,
                    UUID.randomUUID(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    0.0f,
                    0.0f,
                    entityTypesClass.getDeclaredField("C").get(null),
                    getMaterialIdNBTQuartz(),
                    vec3D
            );

            return packet;
        } else if(isVersionSupOrEqualThan("1_17")) {
            Class entityTypesClass = Class.forName("net.minecraft.world.entity.EntityTypes");
            Field f = packetSpawnEntityClass.getDeclaredField("f");
            f.setAccessible(true);
            Field g = packetSpawnEntityClass.getDeclaredField("g");
            g.setAccessible(true);
            Field h = packetSpawnEntityClass.getDeclaredField("h");
            h.setAccessible(true);
            Field i = packetSpawnEntityClass.getDeclaredField("i");
            i.setAccessible(true);
            Field j = packetSpawnEntityClass.getDeclaredField("j");
            j.setAccessible(true);
            Class vec3DClass = Class.forName("net.minecraft.world.phys.Vec3D");
            Object vec3D = vec3DClass.getDeclaredConstructors()[0].newInstance(0.0d, 0.0d, 0.0d);
            Object packet = packetSpawnEntityClass.getDeclaredConstructors()[4].newInstance(
                    id,
                    UUID.randomUUID(),
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    0.0f,
                    0.0f,
                    entityTypesClass.getDeclaredField("C").get(null),
                    getMaterialIdNBTQuartz(),
                    vec3D);
            f.set(packet, 0);
            g.set(packet, 0);
            h.set(packet, 0);
            i.set(packet, 0);
            j.set(packet, 0);
            return packet;
        }else if(isVersionSupOrEqualThan("1_14")){
            Class entityTypesClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".EntityTypes");
            Field a = packetSpawnEntityClass.getDeclaredField("a");
            a.setAccessible(true);
            Field b = packetSpawnEntityClass.getDeclaredField("b");
            b.setAccessible(true);
            Field c = packetSpawnEntityClass.getDeclaredField("c");
            c.setAccessible(true);
            Field d = packetSpawnEntityClass.getDeclaredField("d");
            d.setAccessible(true);
            Field e = packetSpawnEntityClass.getDeclaredField("e");
            e.setAccessible(true);
            Field f = packetSpawnEntityClass.getDeclaredField("f");
            f.setAccessible(true);
            Field g = packetSpawnEntityClass.getDeclaredField("g");
            g.setAccessible(true);
            Field h = packetSpawnEntityClass.getDeclaredField("h");
            h.setAccessible(true);
            Field i = packetSpawnEntityClass.getDeclaredField("i");
            i.setAccessible(true);
            Field j = packetSpawnEntityClass.getDeclaredField("j");
            j.setAccessible(true);
            Field k = packetSpawnEntityClass.getDeclaredField("k");
            k.setAccessible(true);
            Field l = packetSpawnEntityClass.getDeclaredField("l");
            l.setAccessible(true);
            Object packet = packetSpawnEntityClass.newInstance();
            a.set(packet, id);
            b.set(packet, UUID.randomUUID());
            c.set(packet, loc.getX());
            d.set(packet, loc.getY());
            e.set(packet, loc.getZ());
            f.set(packet, 0);
            g.set(packet, 0);
            h.set(packet, 0);
            i.set(packet, 0);
            j.set(packet, 0);
            k.set(packet, entityTypesClass.getDeclaredField("FALLING_BLOCK").get(null));
            l.set(packet, getMaterialIdNBTQuartz());
            return packet;
        }else if(isVersionSupOrEqualThan("1_10")){
            Field a = packetSpawnEntityClass.getDeclaredField("a");
            a.setAccessible(true);
            Field b = packetSpawnEntityClass.getDeclaredField("b");
            b.setAccessible(true);
            Field c = packetSpawnEntityClass.getDeclaredField("c");
            c.setAccessible(true);
            Field d = packetSpawnEntityClass.getDeclaredField("d");
            d.setAccessible(true);
            Field e = packetSpawnEntityClass.getDeclaredField("e");
            e.setAccessible(true);
            Field f = packetSpawnEntityClass.getDeclaredField("f");
            f.setAccessible(true);
            Field g = packetSpawnEntityClass.getDeclaredField("g");
            g.setAccessible(true);
            Field h = packetSpawnEntityClass.getDeclaredField("h");
            h.setAccessible(true);
            Field i = packetSpawnEntityClass.getDeclaredField("i");
            i.setAccessible(true);
            Field j = packetSpawnEntityClass.getDeclaredField("j");
            j.setAccessible(true);
            Field k = packetSpawnEntityClass.getDeclaredField("k");
            k.setAccessible(true);
            Field l = packetSpawnEntityClass.getDeclaredField("l");
            l.setAccessible(true);
            Object packet = packetSpawnEntityClass.newInstance();
            a.set(packet, id);
            b.set(packet, UUID.randomUUID());
            c.set(packet, loc.getX());
            d.set(packet, loc.getY());
            e.set(packet, loc.getZ());
            f.set(packet, 0);
            g.set(packet, 0);
            h.set(packet, 0);
            i.set(packet, 0);
            j.set(packet, 0);
            k.set(packet, 70);
            l.set(packet, getMaterialIdNBTQuartz());
            return packet;
        }
        return null;
    }

    public String getDataWatcherRegistryFieldBoolean(){
        if (isVersionSupOrEqualThan("1_20")) {
            return "k";
        } else if (isVersionSupOrEqualThan("1_19")) {
            return "j";
        } else if (isVersionSupOrEqualThan("1_13")) {
            return "i";
        } else if (isVersionSupOrEqualThan("1_10")) {
            return "h";
        } else {
            return null;
        }
    }

    public String getDataWatcherRegistryFieldBlockPosition(){
        if (isVersionSupOrEqualThan("1_20")) {
            return "n";
        } else if (isVersionSupOrEqualThan("1_19")) {
            return "m";
        } else {
            return null;
        }
    }

    public String getEntityTypeFallingBlockField(){
        if (isVersionSupOrEqualThan("1_20")) {
            return "L";
        } else if (isVersionSupOrEqualThan("1_19")) {
            return "F";
        } else {
            return null;
        }
    }

    // Determines the values used in getMaterialIdNBTQuartz()
    public static int getQuartzIDNMS() {
        try {
            Class blocksClass = Class.forName("net.minecraft.world.level.block.Blocks");
            Object quartzBlock = blocksClass.getField("hd").get(null); // hd for 1.20.1 and 1.20.2 ; gK for 1.19.3 ; fM for 1.18.2

            Class blockClass = Class.forName("net.minecraft.world.level.block.Block");
            Field blockDataField = blockClass.getDeclaredField("d");
            blockDataField.setAccessible(true);

            Object blockData = blockDataField.get(quartzBlock);

            int id = (int) blockClass.getMethod("i", Class.forName("net.minecraft.world.level.block.state.IBlockData")).invoke(null, blockData);

            Bukkit.broadcastMessage(id + "");

            return id;
        } catch (Exception ex) {
            //Bukkit.broadcastMessage("BRIAT");
            return 0;
        }
    }

    // To get the value, replace by getQuartzIDNMS()
    public int getMaterialIdNBTQuartz(){
        if (isVersionSupOrEqualThan("1_20_2")) {
            return 9235;
        } else if (isVersionSupOrEqualThan("1_20")) {
            return 9095;
        } else if (isVersionSupOrEqualThan("1_19")) {
            return 8839;
        } else if (isVersionSupOrEqualThan("1_18")){
            return 6944;
        } else if (isVersionSupOrEqualThan("1_16")){
            return 6742;
        }else if(isVersionSupOrEqualThan("1_14")){
            return 6202;
        }else if(isVersionSupOrEqualThan("1_13")){
            return 5696;
        }else{
            return 155;
        }
    }

    public boolean isGlowAvailable(){
        return ItemSorter.versionHandler.isVersionSupOrEqualThan("1_10");
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
                Class<?> nBTTagCompound = Class.forName("net.minecraft.server." + ItemSorter.version + ".NBTTagCompound");
                tags = nBTTagCompound.newInstance();
            }
            return tags;
        }catch(Exception ex){
            return null;
        }
    }

    public void newEvent(Plugin plug){
        if(isVersionSupOrEqualThan("1_13")){
            Bukkit.getPluginManager().registerEvents(new ServerLoadEvent(), plug);
        }
    }

    public void glow(Location loc, Player p, int id){
        try {
            if(isVersionSupOrEqualThan("1_19")){
                Class packetSpawnEntityClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
                Object packetSpawnEntity = getpacketSpawnEntityFallingBlock(packetSpawnEntityClass, loc, id);
                sendPacket(p, packetSpawnEntity);

                Class dataWatcherClass = Class.forName("net.minecraft.network.syncher.DataWatcher");
                Class dataWatcher_ItemClass = dataWatcherClass.getClasses()[0];
                Class dataWatcherObjectClass = Class.forName("net.minecraft.network.syncher.DataWatcherObject");
                Class DataWatcherRegistryClass = Class.forName("net.minecraft.network.syncher.DataWatcherRegistry");
                Method dataWatcher_ItemClass_to_b = dataWatcher_ItemClass.getMethod("e");

                Class blockPositionClass = Class.forName("net.minecraft.core.BlockPosition");
                Object blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                ArrayList<Object> list = new ArrayList<>();
                list.add(dataWatcher_ItemClass_to_b.invoke(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(0, DataWatcherRegistryClass.getDeclaredField("a").get(null)), (byte) 64)));
                list.add(dataWatcher_ItemClass_to_b.invoke(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(5, DataWatcherRegistryClass.getDeclaredField(getDataWatcherRegistryFieldBoolean()).get(null)), true)));
                list.add(dataWatcher_ItemClass_to_b.invoke(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(8, DataWatcherRegistryClass.getDeclaredField(getDataWatcherRegistryFieldBlockPosition()).get(null)), blockPosition)));

                Class packetEntityMetadataClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");

                Constructor<?> packetEntityMetadataClassConstructor = null;
                for (Constructor<?> constructor : packetEntityMetadataClass.getConstructors()) {
                    if (constructor.getParameterCount() == 2) {
                        packetEntityMetadataClassConstructor = constructor;
                    }
                }
                assert packetEntityMetadataClassConstructor != null;
                Object packetEntityMetadata = packetEntityMetadataClassConstructor.newInstance(id, list);

                sendPacket(p, packetEntityMetadata);
            } else if (isVersionSupOrEqualThan("1_18")) {
                Class packetSpawnEntityClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
                Object packetSpawnEntity = ItemSorter.versionHandler.getpacketSpawnEntityFallingBlock(packetSpawnEntityClass, loc, id);
                ArrayList<Object> list = new ArrayList<>();
                Class dataWatcherClass = Class.forName("net.minecraft.network.syncher.DataWatcher");
                Class dataWatcher_ItemClass = dataWatcherClass.getClasses()[0];
                Class dataWatcherObjectClass = Class.forName("net.minecraft.network.syncher.DataWatcherObject");
                Class DataWatcherRegistryClass = Class.forName("net.minecraft.network.syncher.DataWatcherRegistry");
                list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(0, DataWatcherRegistryClass.getDeclaredField("a").get(null)), (byte) 64));
                list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(5, DataWatcherRegistryClass.getDeclaredField(getDataWatcherRegistryFieldBoolean()).get(null)), true));
                Class entityClass = Class.forName("net.minecraft.world.entity.Entity");
                Object dataWatcher = dataWatcherClass.getDeclaredConstructor(entityClass).newInstance(new Object[]{null});
                Field f = dataWatcherClass.getDeclaredField("f");
                f.setAccessible(true);
                Class objectArrayMapClass = Class.forName("it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap");
                Object objectArrayMap = objectArrayMapClass.getDeclaredConstructor(int[].class, Object[].class).newInstance(new int[]{0, 1}, list.toArray());
                f.set(dataWatcher, objectArrayMap);
                Class packetEntityMetadataClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
                Object packetEntityMetadata = packetEntityMetadataClass.getConstructor(int.class, dataWatcherClass, boolean.class).newInstance(id, dataWatcher, true);
                Field b = packetEntityMetadataClass.getDeclaredField("b");
                b.setAccessible(true);
                b.set(packetEntityMetadata, list);
                sendPacket(p, packetSpawnEntity);
                sendPacket(p, packetEntityMetadata);
            } else if (isVersionSupOrEqualThan("1_17")) {
                Class packetSpawnEntityClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
                Object packetSpawnEntity = getpacketSpawnEntityFallingBlock(packetSpawnEntityClass, loc, id);
                ArrayList<Object> list = new ArrayList<>();
                Class dataWatcherClass = Class.forName("net.minecraft.network.syncher.DataWatcher");
                Class dataWatcher_ItemClass = dataWatcherClass.getClasses()[0];
                Class dataWatcherObjectClass = Class.forName("net.minecraft.network.syncher.DataWatcherObject");
                Class DataWatcherRegistryClass = Class.forName("net.minecraft.network.syncher.DataWatcherRegistry");
                list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(0, DataWatcherRegistryClass.getDeclaredField("a").get(null)), (byte) 64));
                list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(5, DataWatcherRegistryClass.getDeclaredField(getDataWatcherRegistryFieldBoolean()).get(null)), true));
                Class entityClass = Class.forName("net.minecraft.world.entity.Entity");
                Object dataWatcher = dataWatcherClass.getDeclaredConstructor(entityClass).newInstance(new Object[]{null});
                Field f = dataWatcherClass.getDeclaredField("f");
                f.setAccessible(true);
                Class objectArrayMapClass = Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap");
                Object objectArrayMap = objectArrayMapClass.getDeclaredConstructor(int[].class, Object[].class).newInstance(new int[]{0, 1}, list.toArray());
                f.set(dataWatcher, objectArrayMap);
                Class packetEntityMetadataClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
                Object packetEntityMetadata = packetEntityMetadataClass.getConstructor(int.class, dataWatcherClass, boolean.class).newInstance(id, dataWatcher, true);
                Field b = packetEntityMetadataClass.getDeclaredField("b");
                b.setAccessible(true);
                b.set(packetEntityMetadata, list);
                sendPacket(p, packetSpawnEntity);
                sendPacket(p, packetEntityMetadata);
            } else {
                Class packetSpawnEntityClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".PacketPlayOutSpawnEntity");
                Object packetSpawnEntity = ItemSorter.versionHandler.getpacketSpawnEntityFallingBlock(packetSpawnEntityClass, loc, id);
                ArrayList<Object> list = new ArrayList<>();
                Class dataWatcherClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".DataWatcher");
                Class dataWatcher_ItemClass = dataWatcherClass.getClasses()[0];
                Class dataWatcherObjectClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".DataWatcherObject");
                Class DataWatcherRegistryClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".DataWatcherRegistry");
                list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(0, DataWatcherRegistryClass.getDeclaredField("a").get(null)), (byte) 64));
                list.add(dataWatcher_ItemClass.getConstructors()[0].newInstance(dataWatcherObjectClass.getConstructors()[0].newInstance(5, DataWatcherRegistryClass.getDeclaredField(ItemSorter.versionHandler.getDataWatcherRegistryFieldBoolean()).get(null)), true));
                Class packetEntityMetadataClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".PacketPlayOutEntityMetadata");
                Field a = packetEntityMetadataClass.getDeclaredField("a");
                a.setAccessible(true);
                Field b = packetEntityMetadataClass.getDeclaredField("b");
                b.setAccessible(true);
                Object packetEntityMetadata = packetEntityMetadataClass.newInstance();
                a.set(packetEntityMetadata, id);
                b.set(packetEntityMetadata, list);
                sendPacket(p, packetSpawnEntity);
                sendPacket(p, packetEntityMetadata);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeGlow(Player p, int id){
        if(isVersionSupOrEqualThan("1_17")){
            try{
                Class packetEntityDestroyClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
                int[] ints = new int[1];
                ints[0] = id;
                Object packet = packetEntityDestroyClass.getDeclaredConstructors()[1].newInstance(ints);
                sendPacket(p, packet);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            try{
                Class packetEntityDestroyClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".PacketPlayOutEntityDestroy");
                Field f = packetEntityDestroyClass.getDeclaredField("a");
                f.setAccessible(true);
                Object packet = packetEntityDestroyClass.newInstance();
                int ints[] = new int[1];
                ints[0] = id;
                f.set(packet, ints);
                sendPacket(p, packet);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void sendPacket(Player p, Object packet) {
        try {
            if (isVersionSupOrEqualThan("1_20_2")) {
                Method handle = p.getClass().getMethod("getHandle");
                Object craftPlayer = handle.invoke(p);
                Object playerConnection = craftPlayer.getClass().getField("c").get(craftPlayer);
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
                Class<?> packetSendListenerClass = Class.forName("net.minecraft.network.PacketSendListener");
                playerConnection.getClass().getMethod("a", packetClass, packetSendListenerClass).invoke(playerConnection, packet, null);
            } else if (isVersionSupOrEqualThan("1_20")) {
                Method handle = p.getClass().getMethod("getHandle");
                Object craftPlayer = handle.invoke(p);
                Object playerConnection = craftPlayer.getClass().getField("c").get(craftPlayer);
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
                playerConnection.getClass().getMethod("a", packetClass).invoke(playerConnection, packet);
            } else if (isVersionSupOrEqualThan("1_18")) {
                Method handle = p.getClass().getMethod("getHandle");
                Object craftPlayer = handle.invoke(p);
                Object playerConnection = craftPlayer.getClass().getField("b").get(craftPlayer);
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
                playerConnection.getClass().getMethod("a", packetClass).invoke(playerConnection, packet);
            } else if (isVersionSupOrEqualThan("1_17")){
                Method handle = p.getClass().getMethod("getHandle");
                Object craftPlayer = handle.invoke(p);
                Object playerConnection = craftPlayer.getClass().getField("b").get(craftPlayer);
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
                playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
            } else {
                Method handle = p.getClass().getMethod("getHandle");
                Object craftPlayer = handle.invoke(p);
                Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
                Class<?> packetClass = Class.forName("net.minecraft.server." + ItemSorter.version + ".Packet");
                playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isVersionSupOrEqualThan(String version){
        Integer[] versionInt = getVersion(version);
        for (int i = 0; i < (Math.max(versionInt.length, VersionHandler.version.length)); i++) {
            if (versionInt.length == i) {
                return true;
            }
            if (VersionHandler.version.length == i) {
                return true;
            }
            if (versionInt[i] > VersionHandler.version[i]) {
                return false;
            }
        }
        return true;
    }

    public static Integer[] getVersion(String version){
        String[] splitedVersion = version.replaceAll("R", "").replaceFirst("v", "").split("_");
        ArrayList<Integer> newVersion = new ArrayList<>();
        for (String values : splitedVersion) {
            if (values.split("\\D").length > 1) {
               break;
            }
            newVersion.add(Integer.parseInt(values));
        }
        return newVersion.toArray(new Integer[0]);
    }
}
