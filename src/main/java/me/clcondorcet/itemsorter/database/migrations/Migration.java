package me.clcondorcet.itemsorter.database.migrations;

import me.clcondorcet.itemsorter.database.Database;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public abstract class Migration {

    public abstract void applyMigration(Database database) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;
    public abstract void applyVersion(Database database) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    public abstract void undoMigration(Database database);
}
