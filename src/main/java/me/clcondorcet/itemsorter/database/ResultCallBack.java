package me.clcondorcet.itemsorter.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public abstract class ResultCallBack {

    public abstract void run(ResultSet result) throws SQLException, ClassNotFoundException;

}
