package me.clcondorcet.itemsorter.database;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.database.migrations.MigrationsManager;
import me.clcondorcet.itemsorter.database.schemas.ItemSorterDBTable;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class DatabaseManager {

    public static DatabaseManager instance;
    public final Database database;
    public boolean isLoaded;

    public DatabaseManager(ItemSorter plugin) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        instance = this;
        File databaseFile = new File(plugin.getDataFolder(), "data.db");
        database = new Database(databaseFile.getAbsolutePath());
        isLoaded = MigrationsManager.applyMigrationFromVersion(getDatabaseVersion(), database);
    }

    private int getDatabaseVersion() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (!database.isTable(ItemSorterDBTable.NAME)) {
            return 0;
        }
        ObjectResultCallBack<Integer> callBack = new ObjectResultCallBack<Integer>() {
            @Override
            public Integer runReturnObject(ResultSet result) throws SQLException {
                if (result.next()) {
                    return result.getInt(ItemSorterDBTable.COL_SCHEMA_VERSION);
                }
                return 0;
            }
        };
        database.get(ItemSorterDBTable.GET_DB_VERSION, callBack);
        return callBack.getValue();
    }
}
