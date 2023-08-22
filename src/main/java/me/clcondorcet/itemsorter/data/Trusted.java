package me.clcondorcet.itemsorter.data;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.database.schemas.SystemsTable;
import me.clcondorcet.itemsorter.database.schemas.TrustedTable;
import me.clcondorcet.itemsorter.utils.AsyncAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

/**
 * @author clcondorcet
 */
public class Trusted {

    public int trustedID;
    public final System sys;
    public String name;
    public UUID uuid;

    public Trusted (System sys, String name, UUID uuid, AsyncAction.ItemSorterRunnable errorCallBack) {
        this.sys = sys;
        this.name = name;
        this.uuid = uuid;
        String uuid_st = null;
        if (uuid != null && Bukkit.getServer().getOnlineMode()) uuid_st = uuid.toString();
        String finalUuid_st = uuid_st;
        sys.addTrusted(this);
        ItemSorter.getInstance().asyncAction.async(() -> {
            this.trustedID = TrustedTable.insertTrusted(sys.systemID, finalUuid_st, name);
        }, () -> {
            sys.removeTrusted(this);
            errorCallBack.run();
        });
    }

    public Trusted (System sys, String name, UUID uuid) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.sys = sys;
        this.name = name;
        this.uuid = uuid;
        String uuid_st = null;
        if (uuid != null && Bukkit.getServer().getOnlineMode()) uuid_st = uuid.toString();
        this.trustedID = TrustedTable.insertTrusted(sys.systemID, uuid_st, name);
        sys.addTrusted(this);
    }

    protected Trusted (int trustedID, System sys, String name, UUID uuid) {
        this.sys = sys;
        this.name = name;
        this.uuid = uuid;
        this.trustedID = trustedID;
        sys.addTrusted(this);
    }

    public void setTrust(Player p) {
        setTrust(p.getName(), p.getUniqueId());
    }

    public void setTrust(String newName, UUID inputUUID) {
        UUID uuidNow = null;
        if (uuid != null) uuidNow = UUID.fromString(uuid.toString());
        String ownerNameNow = this.name;

        UUID newUUID = inputUUID;
        if (!Bukkit.getServer().getOnlineMode()) {
            newUUID = null;
        }
        if (!Objects.equals(name, newName) && !Objects.equals(newUUID, uuid)) {
            uuid = newUUID;
            name = newName;
            ItemSorter.getInstance().asyncAction.async(() -> {
                TrustedTable.setName(newName, trustedID);
            }, () -> {
                name = ownerNameNow;
                ItemSorter.getInstance().getLogger().warning("Trusted name ('" + newName + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + ownerNameNow + "'");
            });
            String uuid_string = null;
            if (newUUID != null) {
                uuid_string = newUUID.toString();
            }
            UUID finalOwnerUUIDNow = uuidNow;
            String finalUuid_string = uuid_string;
            ItemSorter.getInstance().asyncAction.async(() -> {
                TrustedTable.setUUID(finalUuid_string, trustedID);
            }, () -> {
                uuid = finalOwnerUUIDNow;
                ItemSorter.getInstance().getLogger().warning("Trusted UUID ('" + finalUuid_string + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + finalOwnerUUIDNow + "'");
            });
        } else if (!Objects.equals(name, newName) && Objects.equals(newUUID, uuid)) {
            name = newName;
            ItemSorter.getInstance().asyncAction.async(() -> {
                TrustedTable.setName(newName, trustedID);
            }, () -> {
                name = ownerNameNow;
                ItemSorter.getInstance().getLogger().warning("Trusted name ('" + newName + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + ownerNameNow + "'");
            });
        } else if (!Objects.equals(newUUID, uuid)) {
            this.uuid = newUUID;
            String uuid_string = null;
            if (newUUID != null) {
                uuid_string = newUUID.toString();
            }
            UUID finalOwnerUUIDNow = uuidNow;
            String finalUuid_string = uuid_string;
            ItemSorter.getInstance().asyncAction.async(() -> {
                TrustedTable.setUUID(finalUuid_string, trustedID);
            }, () -> {
                uuid = finalOwnerUUIDNow;
                ItemSorter.getInstance().getLogger().warning("Trusted UUID ('" + finalUuid_string + "') of base '" + this.name + "' failed to update in the database. Reverting to initial value -> '" + finalOwnerUUIDNow + "'");
            });
        }
    }

    public boolean isPlayer(Player p) {
        boolean uuidNull = uuid == null;
        boolean sameUUID = p.getUniqueId().equals(uuid);
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        boolean sameName = p.getName().equals(name);
        if (!uuidNull) {
            if ((sameName && sameUUID) || (!sameName && !sameUUID)) {
                if (!onlineMode) setTrust(name, null);
                return sameName;
            } else if (!sameName) {
                setTrust(p);
                return true;
            } else if (!onlineMode) {
                setTrust(name, null);
                return true;
            }
        } else if (sameName) {
            setTrust(p);
            return true;
        }
        return false;
    }

    public void delete(boolean dbUpdate) {
        sys.removeTrusted(this);
        if (dbUpdate) {
            ItemSorter.getInstance().asyncAction.async(() -> {
                TrustedTable.deleteTrusted(trustedID);
            }, () -> {
                ItemSorter.getInstance().getLogger().warning("An error occurred. Normally it won't affect your world. If it did, please try contact clcondorcet (owner of the plugin).");
            });
        }
    }
}
