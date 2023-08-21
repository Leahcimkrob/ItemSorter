package me.clcondorcet.itemsorter.database.migrations;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.database.Database;
import org.bukkit.Bukkit;

/**
 * @author clcondorcet
 */
public class MigrationsManager {

    public static final int LAST_DB_VERSION = 1;
    public static final Migration[] MIGRATIONS = new Migration[]{
            new Migration_0_to_1()
    };
    public static boolean migrationsDone = false;

    public static boolean applyMigrationFromVersion(int versionFrom, Database database) {
        ItemSorter.getInstance().getLogger().info("Checking for database migrations ... Your version: " + versionFrom + " last DB version: " + LAST_DB_VERSION);
        if (LAST_DB_VERSION > MIGRATIONS.length) {
            ItemSorter.getInstance().getLogger().severe("WTF are you thinking CLCONDORCET ! You missed a DB migration !!!");
            Bukkit.shutdown();
            return false;
        }
        if (versionFrom < LAST_DB_VERSION) {
            ItemSorter.getInstance().getLogger().info("Your database version can be updated. Migration in progress ...");
            for (int i = versionFrom; i < LAST_DB_VERSION; i++) {
                ItemSorter.getInstance().getLogger().info("Applying migration number " + (i+1) +" ...");
                Migration migrator = MIGRATIONS[i];
                try {
                    migrator.applyMigration(database);
                    migrator.applyVersion(database); // Done AFTER the migration.
                } catch (Exception ex) {
                    try {
                        migrator.undoMigration(database);
                    } catch (Throwable ignored) {}
                    ItemSorter.getInstance().getLogger().severe("DB migration from version " + i + " FAILED. Unload Plugin. Error:");
                    ex.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(ItemSorter.getInstance());
                    return false;
                }
            }
            ItemSorter.getInstance().getLogger().info("All migrations are done!");
        } else {
            ItemSorter.getInstance().getLogger().info("No migration needed. Your on the last DB version.");
        }
        migrationsDone = true;
        return true;
    }
}
