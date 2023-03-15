package me.clcondorcet.itemsorter.data;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.caching.CachedMap;
import me.clcondorcet.itemsorter.database.DatabaseManager;
import me.clcondorcet.itemsorter.database.ResultCallBack;
import me.clcondorcet.itemsorter.database.schemas.*;
import me.clcondorcet.itemsorter.utils.FutureLocation;
import me.clcondorcet.itemsorter.utils.Pair;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author clcondorcet
 */
public class DataManager {

    private static final HashMap<Integer, System> systems = new HashMap<>();
    public static final HashMap<FutureLocation, System> bases = new HashMap<>();
    public static final HashMap<FutureLocation, Deposit> deposits = new HashMap<>();
    public static final HashMap<FutureLocation, Filter> filter = new HashMap<>();
    private static final HashMap<Integer, System> notLoadedSystems = new HashMap<>(); // System is in an unloaded map
    private static final ArrayList<System> loadingSystems = new ArrayList<>(); // System is under loading (async sql)
    public static final HashMap<String, ArrayList<System>> systemToCheck = new HashMap<>();
    public static CachedItems cachedItems;
    public static final HashMap<Player, InFilterObject> inFilter = new HashMap<>();
    public static final HashMap<Player, InSystemObject> inSystem = new HashMap<>();
    public static final CachedMap<Pair<Integer, org.bukkit.Material>, Filter> cachedFullFilters = new CachedMap<>(30, TimeUnit.SECONDS);

    public static Collection<System> getSystems() {
        return new ArrayList<>(systems.values());
    }

    public static Collection<System> getLoadingSystems() {
        return new ArrayList<>(loadingSystems);
    }

    public static void addSystem(System sys) {
        loadingSystems.remove(sys);
        if (notLoadedSystems.containsKey(sys.systemID)) {
            removeSystem(sys);
        }
        bases.put(sys.baseLoc, sys);
        bases.put(sys.sign, sys);
        sys.loaded = true;
        systems.put(sys.systemID, sys);
    }

    public static void addLoadingSystem(System sys) {
        loadingSystems.add(sys);
    }

    public static boolean removeLoadingSystem(System sys) {
        bases.remove(sys.baseLoc, sys);
        bases.remove(sys.sign, sys);
        return loadingSystems.remove(sys);
    }

    public static System getSystemFromId(int systemID) {
        System system = systems.get(systemID);
        if (system == null) {
            system = notLoadedSystems.get(systemID);
        }
        return system;
    }

    public static void setNotLoadedSystem(System system) {
        removeSystem(system);
        loadingSystems.remove(system);
        bases.put(system.baseLoc, system);
        bases.put(system.sign, system);
        notLoadedSystems.put(system.systemID, system);
        if (!systemToCheck.containsKey(system.baseLoc.getWorldName())) {
            systemToCheck.put(system.baseLoc.getWorldName(), new ArrayList<>());
        }
        systemToCheck.get(system.baseLoc.getWorldName()).add(system);
    }

    public static void removeSystem(System sys) {
        sys.loaded = false;
        systems.remove(sys.systemID);
        loadingSystems.remove(sys);
        bases.remove(sys.baseLoc, sys);
        bases.remove(sys.sign, sys);
        notLoadedSystems.remove(sys.systemID);
        if (systemToCheck.containsKey(sys.baseLoc.getWorldName())) {
            systemToCheck.get(sys.baseLoc.getWorldName()).remove(sys);
        }
    }

    public static void loadData() {
        try {
            cachedItems = new CachedItems();
            loadSystemsTable();
            loadTrustedTable();
            loadDepositsTable();
            loadFiltersTable();
            loadMaterialsTable();
        } catch (Exception ex) {
            ItemSorter.getInstance().getLogger().severe("An error occurred when loading the data from the SQL database. Unloading plugin ...");
            ex.printStackTrace();
            ItemSorter.getInstance().getPluginLoader().disablePlugin(ItemSorter.getInstance());
        }
    }

    private static void loadSystemsTable() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ResultCallBack callBack = new ResultCallBack() {
            @Override
            public void run(ResultSet result) throws SQLException {
                ArrayList<System> toDelete = new ArrayList<>();
                while (result.next()) {
                    UUID ownerUUID;
                    try {
                        ownerUUID = UUID.fromString(result.getString(SystemsTable.COL_OWNER_UUID));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        ownerUUID = null;
                    }
                    String worldName = result.getString(SystemsTable.COL_WORLD);
                    System sys = new System(
                            result.getInt(SystemsTable.COL_SYSTEM_ID),
                            result.getString(SystemsTable.COL_NAME),
                            new FutureLocation(
                                    worldName,
                                    (double) result.getInt(SystemsTable.COL_BLOCK_X),
                                    (double) result.getInt(SystemsTable.COL_BLOCK_Y),
                                    (double) result.getInt(SystemsTable.COL_BLOCK_Z)
                            ),
                            new FutureLocation(
                                    worldName,
                                    (double) result.getInt(SystemsTable.COL_SIGN_X),
                                    (double) result.getInt(SystemsTable.COL_SIGN_Y),
                                    (double) result.getInt(SystemsTable.COL_SIGN_Z)
                            ),
                            result.getString(SystemsTable.COL_OWNER_NAME),
                            ownerUUID
                    );
                    try {
                        if (sys.checkExistsInWorld()) {
                            addSystem(sys);
                        } else {
                            toDelete.add(sys);
                        }
                    } catch (FutureLocation.WorldNotLoaded e) {
                        setNotLoadedSystem(sys);
                    }
                }
                for(System sys : toDelete){
                    ItemSorter.getInstance().getLogger().info("The base " + sys.name + " will be deleted because the base is not longer at the same location.");
                    sys.delete(true, false);
                }
            }
        };
        DatabaseManager.instance.database.get(SystemsTable.SELECT_ALL_SYSTEMS, callBack);
    }

    private static void loadTrustedTable() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ResultCallBack callBack = new ResultCallBack() {
            @Override
            public void run(ResultSet result) throws SQLException {
                ArrayList<Integer> toDelete = new ArrayList<>();
                while(result.next()) {
                    UUID trustedUUID;
                    try {
                        trustedUUID = UUID.fromString(result.getString(TrustedTable.COL_TRUSTED_UUID));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        trustedUUID = null;
                    }
                    int trustedID = result.getInt(TrustedTable.COL_TRUSTED_ID);
                    System fromSystem = getSystemFromId(result.getInt(TrustedTable.COL_SYSTEM_ID));
                    if (fromSystem != null) {
                        new Trusted(
                                trustedID,
                                fromSystem,
                                result.getString(TrustedTable.COL_TRUSTED_NAME),
                                trustedUUID
                        );
                    } else {
                        toDelete.add(trustedID);
                    }
                }
                for(Integer trustedID : toDelete){
                    try {
                        TrustedTable.deleteTrusted(trustedID);
                    } catch (Exception ignored) {}
                }
            }
        };
        DatabaseManager.instance.database.get(TrustedTable.SELECT_ALL_TRUSTED, callBack);
    }

    private static void loadDepositsTable() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ResultCallBack callBack = new ResultCallBack() {
            @Override
            public void run(ResultSet result) throws SQLException {
                ArrayList<Integer> toDelete = new ArrayList<>();
                while(result.next()) {
                    int depositID = result.getInt(DepositsTable.COL_DEPOSIT_ID);
                    String worldName = result.getString(DepositsTable.COL_WORLD);
                    System fromSystem = getSystemFromId(result.getInt(DepositsTable.COL_SYSTEM_ID));
                    if (fromSystem != null) {
                        Deposit depo = new Deposit(
                                depositID,
                                fromSystem,
                                new FutureLocation(
                                        worldName,
                                        (double) result.getInt(DepositsTable.COL_X),
                                        (double) result.getInt(DepositsTable.COL_Y),
                                        (double) result.getInt(DepositsTable.COL_Z)
                                ),
                                new FutureLocation(
                                        worldName,
                                        (double) result.getInt(DepositsTable.COL_SIGN_X),
                                        (double) result.getInt(DepositsTable.COL_SIGN_Y),
                                        (double) result.getInt(DepositsTable.COL_SIGN_Z)
                                )
                        );
                        try {
                            if (!depo.checkExistsInWorld()) {
                                depo.delete(false, false, true);
                                toDelete.add(depositID);
                            }
                        } catch (FutureLocation.WorldNotLoaded ignored) {}
                    } else {
                        toDelete.add(depositID);
                    }
                }
                for(Integer depositID : toDelete){
                    try {
                        DepositsTable.deleteDeposit(depositID);
                    } catch (Exception ignore) {}
                }
            }
        };
        DatabaseManager.instance.database.get(DepositsTable.SELECT_ALL_DEPOSITS, callBack);
    }

    private static void loadFiltersTable() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ResultCallBack callBack = new ResultCallBack() {
            @Override
            public void run(ResultSet result) throws SQLException {
                ArrayList<Integer> toDelete = new ArrayList<>();
                while(result.next()) {
                    int filterID = result.getInt(FiltersTable.COL_FILTER_ID);
                    String worldName = result.getString(FiltersTable.COL_WORLD);
                    System fromSystem = getSystemFromId(result.getInt(FiltersTable.COL_SYSTEM_ID));
                    if (fromSystem != null) {
                        Filter filter = new Filter(
                                filterID,
                                fromSystem,
                                new FutureLocation(
                                        worldName,
                                        (double) result.getInt(FiltersTable.COL_X),
                                        (double) result.getInt(FiltersTable.COL_Y),
                                        (double) result.getInt(FiltersTable.COL_Z)
                                ),
                                new FutureLocation(
                                        worldName,
                                        (double) result.getInt(FiltersTable.COL_SIGN_X),
                                        (double) result.getInt(FiltersTable.COL_SIGN_Y),
                                        (double) result.getInt(FiltersTable.COL_SIGN_Z)
                                ),
                                result.getBoolean(FiltersTable.COL_IS_TRASH),
                                result.getInt(FiltersTable.COL_TRASH_PRIORITY)
                        );
                        try {
                            if (!filter.checkExistsInWorld()) {
                                filter.delete(false, false, true);
                                toDelete.add(filterID);
                            }
                        } catch (FutureLocation.WorldNotLoaded ignored) {}
                    } else {
                        toDelete.add(filterID);
                    }
                }
                for(Integer depositID : toDelete){
                    try {
                        FiltersTable.deleteFilter(depositID);
                    } catch (Exception ignored) {}
                }
            }
        };
        DatabaseManager.instance.database.get(FiltersTable.SELECT_ALL_FILTERS, callBack);
    }

    private static class MaterialData {
        public int systemID;
        public int filterID;
        public String material;

        MaterialData (int systemID, int filterID, String material) {
            this.systemID = systemID;
            this.filterID = filterID;
            this.material = material;
        }
    }

    private static void loadMaterialsTable() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ResultCallBack callBack = new ResultCallBack() {
            @Override
            public void run(ResultSet result) throws SQLException, ClassNotFoundException {
                ArrayList<MaterialData> toDelete = new ArrayList<>();
                while(result.next()) {
                    MaterialData mData = new MaterialData(result.getInt(MaterialsTable.COL_SYSTEM_ID), result.getInt(MaterialsTable.COL_FILTER_ID), result.getString(MaterialsTable.COL_MATERIAL));
                    System fromSystem = getSystemFromId(mData.systemID);
                    if (fromSystem != null) {
                        Filter fromFilter = fromSystem.getFilterFromId(mData.filterID);
                        if (fromFilter != null) {
                            try {
                                org.bukkit.Material newMat = org.bukkit.Material.getMaterial(mData.material);
                                if (newMat != null) {
                                    new Material(
                                            fromFilter,
                                            newMat,
                                            result.getInt(MaterialsTable.COL_PRIORITY),
                                            false,
                                            () -> {}
                                    );
                                } else {
                                    toDelete.add(mData);
                                }
                            } catch (ClassCastException | NullPointerException ex) {
                                toDelete.add(mData);
                            }
                        } else {
                            toDelete.add(mData);
                        }
                    } else {
                        toDelete.add(mData);
                    }
                }
                for(MaterialData mData : toDelete){
                    try {
                        MaterialsTable.deleteMaterial(mData.systemID, mData.filterID, mData.material);
                    } catch (Exception ignored) {}
                }
            }
        };
        DatabaseManager.instance.database.get(MaterialsTable.SELECT_ALL_MATERIALS, callBack);
    }
}
