package me.clcondorcet.itemsorter.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import static java.lang.Math.*;

/**
 * @author clcondorcet
 */
public class FutureLocation implements Cloneable {
    private Double x;
    private Double y;
    private Double z;

    private Float yaw;
    private Float pitch;

    private String worldName;

    private Location location = null;

    public Double getX() {
        return x;
    }

    public int getBlockX() {
        return Location.locToBlock(x);
    }

    public void setX(Double x) {
        this.x = x;
        if(location != null) this.location.setX(x);
    }

    public Double getY() {
        return y;
    }

    public int getBlockY() {
        return Location.locToBlock(y);
    }

    public void setY(Double y) {
        this.y = y;
        if(location != null) this.location.setY(y);
    }

    public Double getZ() {
        return z;
    }

    public int getBlockZ() {
        return Location.locToBlock(z);
    }

    public void setZ(Double z) {
        this.z = z;
        if(location != null) this.location.setZ(z);
    }

    public Float getYaw() {
        return yaw;
    }

    public void setYaw(Float yaw) {
        this.yaw = yaw;
        if(location != null) this.location.setYaw(yaw);
    }

    public Float getPitch() {
        return pitch;
    }

    public void setPitch(Float pitch) {
        this.pitch = pitch;
        if(location != null) this.location.setPitch(pitch);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Location getLocation() {
        return location;
    }

    public FutureLocation(String worldName, Double x, Double y, Double z, Float yaw, Float pitch){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public FutureLocation(String worldName, Double x, Double y, Double z){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0F;
        this.pitch = 0F;
    }

    public FutureLocation(World world, Double x, Double y, Double z, Float yaw, Float pitch){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.location = new Location(world, x, y, z, yaw, pitch);
    }

    public FutureLocation(World world, Double x, Double y, Double z){
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0F;
        this.pitch = 0F;
        this.location = new Location(world, x, y, z, 0F, 0F);
    }

    public FutureLocation(Location location){
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.location = location;
    }

    public Location build() throws WorldNotLoaded {
        if (location != null) return location;
        World world = Bukkit.getWorld(worldName);
        if(world != null){
            location = new Location(world, x, y, z, yaw, pitch);
            return location;
        } else throw new WorldNotLoaded("The world $worldName is not loaded. Location cannot be built!");
    }

    public Vector getDirection() {
        Vector vector = new Vector();
        double rotX = this.yaw.doubleValue();
        double rotY = this.pitch.doubleValue();
        vector.setY(-sin(Math.toRadians(rotY)));
        double xz = cos(Math.toRadians(rotY));
        vector.setX(-xz * sin(Math.toRadians(rotX)));
        vector.setZ(xz * cos(Math.toRadians(rotX)));
        return vector;
    }

    public FutureLocation setDirection(Vector vector) {
        double x = vector.getX();
        double z = vector.getZ();
        if (x == 0.0 && z == 0.0) {
            if (vector.getY() > 0.0) {
                this.setPitch(-90F);
            } else {
                this.setPitch(90F);
            }
        } else {
            double theta = atan2(-x, z);
            this.setYaw((float) Math.toDegrees((theta + 6.283185307179586) % 6.283185307179586));
            double x2 = NumberConversions.square(x);
            double z2 = NumberConversions.square(z);
            double xz = sqrt(x2 + z2);
            this.setPitch((float) Math.toDegrees(atan(-vector.getY() / xz)));
        }
        return this;
    }

    public boolean sameWorld(FutureLocation loc) {
        return sameWorld(loc, false);
    }

    public boolean sameWorld(FutureLocation loc, Boolean strict) {
        if (this.location != null && loc.location != null && this.location.getWorld() != null && loc.location.getWorld() != null) {
            return this.location.getWorld() == loc.location.getWorld();
        } else {
            if (strict) {
                return this.worldName.equals(loc.worldName);
            } else {
                return this.worldName.equalsIgnoreCase(loc.worldName);
            }
        }
    }

    public boolean sameWorld(Location loc) {
        return sameWorld(loc, false);
    }

    public boolean sameWorld(Location loc, Boolean strict) {
        if (this.location != null) {
            if (this.location.getWorld() != null && loc.getWorld() != null) {
                return this.location.getWorld() == loc.getWorld();
            } else {
                if(loc.getWorld() != null){
                    if (strict) {
                        return this.worldName.equals(loc.getWorld().getName());
                    } else {
                        return this.worldName.equalsIgnoreCase(loc.getWorld().getName());
                    }
                } else {
                    throw new IllegalArgumentException("Cannot measure equality with a null world.");
                }
            }
        } else {
            if(loc.getWorld() != null){
                if (strict) {
                    return this.worldName.equals(loc.getWorld().getName());
                } else {
                    return this.worldName.equalsIgnoreCase(loc.getWorld().getName());
                }
            }else{
                throw new IllegalArgumentException("Cannot measure equality with a null world.");
            }
        }
    }

    public FutureLocation add(FutureLocation vec) {
        if (sameWorld(vec)) {
            this.setX(this.x + vec.x);
            this.setY(this.y + vec.y);
            this.setZ(this.z + vec.z);
            return this;
        } else {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
    }

    public FutureLocation add(Location vec) {
        if (sameWorld(vec)) {
            this.setX(this.x + vec.getX());
            this.setY(this.y + vec.getY());
            this.setZ(this.z + vec.getZ());
            return this;
        } else {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
    }

    public FutureLocation add(Vector vec) {
        this.setX(this.x + vec.getX());
        this.setY(this.y + vec.getY());
        this.setZ(this.z + vec.getZ());
        return this;
    }

    public FutureLocation add(Double x, Double y, Double z) {
        this.setX(this.x + x);
        this.setY(this.y + y);
        this.setZ(this.z + z);
        return this;
    }

    public FutureLocation addUnsigned(Double x, Double y, Double z) {
        this.setX(this.x + (this.x > 0 ? x : -x));
        this.setY(this.y + (this.y > 0 ? y : -y));
        this.setZ(this.z + (this.z > 0 ? z : -z));
        return this;
    }

    public FutureLocation subtract(FutureLocation vec) {
        if (sameWorld(vec)) {
            this.setX(this.x - vec.x);
            this.setY(this.y - vec.y);
            this.setZ(this.z - vec.z);
            return this;
        } else {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
    }

    public FutureLocation subtract(Location vec) {
        if (sameWorld(vec)) {
            this.setX(this.x - vec.getX());
            this.setY(this.y - vec.getY());
            this.setZ(this.z - vec.getZ());
            return this;
        } else {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
    }

    public FutureLocation subtract(Vector vec) {
        this.setX(this.x - vec.getX());
        this.setY(this.y - vec.getY());
        this.setZ(this.z - vec.getZ());
        return this;
    }

    public FutureLocation subtract(Double x, Double y, Double z) {
        this.setX(this.x - x);
        this.setY(this.y - y);
        this.setZ(this.z - z);
        return this;
    }

    public Double length() {
        return sqrt(lengthSquared());
    }

    public Double lengthSquared() {
        return NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z);
    }

    public Double distance(FutureLocation loc) {
        return sqrt(distanceSquared(loc));
    }

    public Double distanceSquared(FutureLocation loc) {
        if (sameWorld(loc)) {
            return NumberConversions.square(x - loc.x) + NumberConversions.square(y - loc.y) + NumberConversions.square(z - loc.z);
        }else{
            throw new IllegalArgumentException("Cannot measure distance between not same world or null worlds");
        }
    }

    public Double distance(Location loc) {
        return sqrt(distanceSquared(loc));
    }

    public Double distanceSquared(Location loc) {
        if (sameWorld(loc)) {
            return NumberConversions.square(x - loc.getX()) + NumberConversions.square(y - loc.getY()) + NumberConversions.square(z - loc.getZ());
        }else{
            throw new IllegalArgumentException("Cannot measure distance between not same world or null worlds");
        }
    }

    public FutureLocation multiply(Double m) {
        this.setX(this.x * m);
        this.setX(this.y * m);
        this.setX(this.z * m);
        return this;
    }

    public FutureLocation zero() {
        this.setX(0D);
        this.setX(0D);
        this.setX(0D);
        return this;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    private long doubleToLongBits(Double value) {
        return Double.doubleToLongBits(value);
    }

    private int floatToIntBits(Float value) {
        return Float.floatToIntBits(value);
    }

    public BlockFace getBlockFace() {
        if(yaw > -45 && yaw < 45){
            return BlockFace.SOUTH;
        }else if(yaw > 45 && yaw < 135){
            return BlockFace.WEST;
        }else if(yaw > -135 && yaw < -45){
            return BlockFace.EAST;
        }else{
            return BlockFace.NORTH;
        }
    }

    public boolean sameBlock(FutureLocation other) {
        return this.sameWorld(other)
                && this.getBlockX() == other.getBlockX()
                && this.getBlockY() == other.getBlockY()
                && this.getBlockZ() == other.getBlockZ();
    }

    public boolean sameBlock(Location other) {
        return this.sameWorld(other)
                && this.getBlockX() == other.getBlockX()
                && this.getBlockY() == other.getBlockY()
                && this.getBlockZ() == other.getBlockZ();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || (!(other instanceof FutureLocation) && !(other instanceof Location))) {
            return false;
        } else {
            if (other instanceof FutureLocation) {
                try {
                    return (sameWorld((FutureLocation) other)
                            && doubleToLongBits(x) == doubleToLongBits(((FutureLocation) other).x)
                            && doubleToLongBits(y) == doubleToLongBits(((FutureLocation) other).y)
                            && doubleToLongBits(z) == doubleToLongBits(((FutureLocation) other).z)
                            && floatToIntBits(yaw) == floatToIntBits(((FutureLocation) other).yaw)
                            && floatToIntBits(pitch) == floatToIntBits(((FutureLocation) other).pitch)
                    );
                }catch (Exception e){
                    return false;
                }
            } else if (other instanceof Location) {
                try {
                    return (sameWorld((Location) other)
                            && doubleToLongBits(x) == doubleToLongBits(((Location) other).getX())
                            && doubleToLongBits(y) == doubleToLongBits(((Location) other).getY())
                            && doubleToLongBits(z) == doubleToLongBits(((Location) other).getZ())
                            && floatToIntBits(yaw) == floatToIntBits(((Location) other).getYaw())
                            && floatToIntBits(pitch) == floatToIntBits(((Location) other).getPitch())
                    );
                }catch (Exception e){
                    return false;
                }
            } else return false;
        }
    }

    @Override
    public int hashCode() {
        if (location != null) return location.hashCode();
        try {
            build();
            return location.hashCode();
        } catch (Exception ignored) {}
        int hash = 3;
        hash *= 19;
        hash = 19 * hash + (int)(doubleToLongBits(this.x) ^ doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(doubleToLongBits(this.y) ^ doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(doubleToLongBits(this.z) ^ doubleToLongBits(this.z) >>> 32);
        hash = 19 * hash + floatToIntBits(this.pitch);
        hash = 19 * hash + floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public FutureLocation clone() {
        FutureLocation newObj = new FutureLocation(worldName, x, y, z, pitch, yaw);
        if (this.location != null) {
            newObj.location = this.location.clone();
        }
        return newObj;
    }

    @Override
    public String toString() {
        return "FutureLocation{worldName=" + worldName + ",x=" + x + ",y=" + y + ",z=" + z + ",yaw=" + yaw + ",pitch=" + pitch + ",location=" + location.toString() + "}";
    }

    public static class WorldNotLoaded extends Exception {
        public WorldNotLoaded(String message) {super(message); }
        public WorldNotLoaded() {super(); }
    }
}
