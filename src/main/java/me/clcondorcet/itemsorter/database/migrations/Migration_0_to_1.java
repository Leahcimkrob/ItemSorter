package me.clcondorcet.itemsorter.database.migrations;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.*;

/**
 * @author clcondorcet
 */
public class Migration_0_to_1 extends Migration {

    private Database database;

    @Override
    public void applyMigration(Database database) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.database = database;
        ItemSorter.getInstance().getLogger().info("[Migration 0->1] (1/2) Creating tables ...");
        createTables();
        ItemSorter.getInstance().getLogger().info("[Migration 0->1] (2/2) Migrating from file to DB ...");
        migrateFromLegacyFile();
        ItemSorter.getInstance().getLogger().info("[Migration 0->1] Done.");
    }

    private static final String CREATE_TABLE_ITEM_SORTER_DB
            = "CREATE TABLE IF NOT EXISTS ItemSorterDB(" +
            "   schema_version INT," +
            "   PRIMARY KEY(schema_version)" +
            ")";
    private static final String CREATE_TABLE_SYSTEMS
            = "CREATE TABLE IF NOT EXISTS Systems(" +
            "    systemID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    name VARCHAR(50) NOT NULL," +
            "    ownerUUID VARCHAR(100)," +
            "    ownerName VARCHAR(50) NOT NULL," +
            "    world VARCHAR(50) NOT NULL," +
            "    x INT NOT NULL," +
            "    y INT NOT NULL," +
            "    z INT NOT NULL," +
            "    signx INT NOT NULL," +
            "    signy INT NOT NULL," +
            "    signz INT NOT NULL," +
            "    UNIQUE(name)" +
            ")";
    private static final String CREATE_TABLE_TRUSTED
            = "CREATE TABLE IF NOT EXISTS Trusted(" +
            "    systemID INTEGER," +
            "    trustedID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    trustedUUID VARCHAR(100)," +
            "    trustedName VARCHAR(50) NOT NULL," +
            "    FOREIGN KEY(systemID) REFERENCES Systems(systemID) ON DELETE CASCADE" +
            ")";
    private static final String CREATE_TABLE_FILTERS
            = "CREATE TABLE IF NOT EXISTS Filters(" +
            "    systemID INTEGER," +
            "    filterID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    trash LOGICAL NOT NULL," +
            "    trashPriority INT NOT NULL," +
            "    world VARCHAR(50) NOT NULL," +
            "    x INT NOT NULL," +
            "    y INT NOT NULL," +
            "    z INT NOT NULL," +
            "    signx INT NOT NULL," +
            "    signy INT NOT NULL," +
            "    signz INT NOT NULL," +
            "    FOREIGN KEY(systemID) REFERENCES Systems(systemID) ON DELETE CASCADE" +
            ")";
    private static final String CREATE_TABLE_MATERIALS
            = "CREATE TABLE IF NOT EXISTS Materials(" +
            "    systemID INTEGER," +
            "    filterID INTEGER," +
            "    material VARCHAR(50) NOT NULL," +
            "    priority INT NOT NULL," +
            "    PRIMARY KEY(systemID, filterID, material)," +
            "    FOREIGN KEY(systemID, filterID) REFERENCES Filters(systemID, filterID) ON DELETE CASCADE" +
            ")";
    private static final String CREATE_TABLE_DEPOSITS
            = "CREATE TABLE IF NOT EXISTS Deposits(" +
            "    systemID INTEGER," +
            "    depositID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    world VARCHAR(50) NOT NULL," +
            "    x INT NOT NULL," +
            "    y INT NOT NULL," +
            "    z INT NOT NULL," +
            "    signx INT NOT NULL," +
            "    signy INT NOT NULL," +
            "    signz INT NOT NULL," +
            "    FOREIGN KEY(systemID) REFERENCES Systems(systemID) ON DELETE CASCADE" +
            ")";

    private void createTables() {
        database.set(CREATE_TABLE_ITEM_SORTER_DB);
        database.set(CREATE_TABLE_SYSTEMS);
        database.set(CREATE_TABLE_TRUSTED);
        database.set(CREATE_TABLE_FILTERS);
        database.set(CREATE_TABLE_MATERIALS);
        database.set(CREATE_TABLE_DEPOSITS);
    }

    public static final String SET_DB_VERSION
            = "INSERT INTO ItemSorterDB (schema_version) values (?)";

    @Override
    public void applyVersion(Database database) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        database.set(SET_DB_VERSION, 1);
    }

    @Override
    public void undoMigration(Database database) {
        File file = new File(ItemSorter.getInstance().getDataFolder(), "data.db");
        file.delete();
    }

    public static final String INSERT_SYSTEM
            = "INSERT INTO Systems (name, ownerUUID, ownerName, world, x, y, z, signx, signy, signz) VALUES (?,?,?,?,?,?,?,?,?,?)";

    public static final String INSERT_TRUSTED
            = "INSERT INTO Trusted (systemID, trustedUUID, trustedName) VALUES (?,?,?)";

    public static final String INSERT_FILTER
            = "INSERT INTO Filters (systemID, trash, trashPriority, world, x, y, z, signx, signy, signz) VALUES (?,?,?,?,?,?,?,?,?,?)";

    public static final String INSERT_MATERIAL
            = "INSERT INTO Materials (systemID, filterID, material, priority) VALUES (?,?,?,?)";

    public static final String INSERT_DEPOSIT
            = "INSERT INTO Deposits (systemID, world, x, y, z, signx, signy, signz) VALUES (?,?,?,?,?,?,?,?)";

    public void migrateFromLegacyFile() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        File datafile = new File(ItemSorter.getInstance().getDataFolder(), "data.yml");
        if (datafile.exists()) {
            ItemSorter.getInstance().getLogger().info("The data.yml file exists (previous version)! All the data will be migrated to the new Database. It may take a while depending on the size of your file...");
            long timeBefore = System.currentTimeMillis();
            ItemSorter.configManager.load(ItemSorter.getInstance().getDataFolder(), "data.yml");
            FileConfiguration data = ItemSorter.configManager.getConfig("data.yml");
            int systems = 0;
            try (Connection con = database.open()) {
                con.setAutoCommit(false);

                for(String system_name : data.getConfigurationSection("data").getKeys(false)){

                    String systemPath = "data." + system_name + ".";

                    String[] baseLoc = data.getString(systemPath + "baseLoc").split(":");
                    String system_world = baseLoc[0];
                    int system_base_x = Integer.parseInt(baseLoc[1]);
                    int system_base_y = Integer.parseInt(baseLoc[2]);
                    int system_base_z = Integer.parseInt(baseLoc[3]);

                    String[] signLoc = data.getString(systemPath + "sign").split(":");
                    int system_sign_x = Integer.parseInt(signLoc[1]);
                    int system_sign_y = Integer.parseInt(signLoc[2]);
                    int system_sign_z = Integer.parseInt(signLoc[3]);

                    String system_owner_name = data.getString(systemPath + "owner");

                    int systemID = database.setAndReturnKey(
                            con,
                            INSERT_SYSTEM,
                            system_name,
                            null, // owner UUID
                            system_owner_name,
                            system_world,
                            system_base_x,
                            system_base_y,
                            system_base_z,
                            system_sign_x,
                            system_sign_y,
                            system_sign_z
                    );

                    for (String trusted_name : data.getStringList(systemPath + "trusts")) {
                        database.set(con, INSERT_TRUSTED, systemID, null, trusted_name);
                    }

                    ConfigurationSection filtersSection = data.getConfigurationSection(systemPath + "filters");
                    if (filtersSection != null) {
                        for (String filterFileID : filtersSection.getKeys(false)) {

                            String filterPath = systemPath + "filters." + filterFileID + ".";

                            Boolean filter_isTrash = data.getBoolean(filterPath + "isTrash");
                            int filter_trash_Priority = data.getInt(filterPath + "TrashPriority");

                            String[] filterLoc = data.getString(filterPath + "loc").split(":");
                            String filter_world = filterLoc[0];
                            int filter_x = Integer.parseInt(filterLoc[1]);
                            int filter_y = Integer.parseInt(filterLoc[2]);
                            int filter_z = Integer.parseInt(filterLoc[3]);

                            String[] filterSignLoc = data.getString(filterPath + "sign").split(":");
                            int filter_sign_x = Integer.parseInt(filterSignLoc[1]);
                            int filter_sign_y = Integer.parseInt(filterSignLoc[2]);
                            int filter_sign_z = Integer.parseInt(filterSignLoc[3]);

                            int filterID = database.setAndReturnKey(
                                    con,
                                    INSERT_FILTER,
                                    systemID,
                                    filter_isTrash,
                                    filter_trash_Priority,
                                    filter_world,
                                    filter_x,
                                    filter_y,
                                    filter_z,
                                    filter_sign_x,
                                    filter_sign_y,
                                    filter_sign_z
                            );

                            for (String material : data.getStringList(filterPath + "materials")) {
                                String[] mat = material.split(":");
                                String material_type = mat[0];
                                int material_priority = Integer.parseInt(mat[1]);
                                database.set(con, INSERT_MATERIAL, systemID, filterID, material_type, material_priority);
                            }

                        }
                    }

                    ConfigurationSection depositsSection = data.getConfigurationSection(systemPath + "deposits");
                    if (depositsSection != null) {
                        for (String depositFileID : depositsSection.getKeys(false)) {

                            String depositPath = systemPath + "deposits." + depositFileID + ".";

                            String[] depositLoc = data.getString(depositPath + "loc").split(":");
                            String deposit_world = depositLoc[0];
                            int deposit_x = Integer.parseInt(depositLoc[1]);
                            int deposit_y = Integer.parseInt(depositLoc[2]);
                            int deposit_z = Integer.parseInt(depositLoc[3]);

                            String[] depositSignLoc = data.getString(depositPath + "sign").split(":");
                            int deposit_sign_x = Integer.parseInt(depositSignLoc[1]);
                            int deposit_sign_y = Integer.parseInt(depositSignLoc[2]);
                            int deposit_sign_z = Integer.parseInt(depositSignLoc[3]);

                            database.set(
                                    con,
                                    INSERT_DEPOSIT,
                                    systemID,
                                    deposit_world,
                                    deposit_x,
                                    deposit_y,
                                    deposit_z,
                                    deposit_sign_x,
                                    deposit_sign_y,
                                    deposit_sign_z
                            );

                        }
                    }

                    systems++;
                }

                con.commit();
            }
            long delay = System.currentTimeMillis() - timeBefore;
            ItemSorter.getInstance().getLogger().info("Migration of " + systems + " systems from data.yml to SQL databases is done (" + delay + "ms).");
            ItemSorter.configManager.dropConfig("data.yml");
            try {
                if (!datafile.renameTo(new File(ItemSorter.getInstance().getDataFolder(), "OLD_data.yml"))) {
                    ItemSorter.getInstance().getLogger().warning("We were not able to rename the data.yml file. That's okay though so don't worry.");
                }
            } catch (Exception ex) {
                ItemSorter.getInstance().getLogger().warning("We were not able to rename the data.yml file. That's okay though so don't worry. The error:");
                ex.printStackTrace();
            }
        }
    }
}
