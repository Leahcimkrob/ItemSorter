package me.clcondorcet.itemsorter.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public abstract class ObjectResultCallBack<V> extends ResultCallBack {
    private V value;

    public abstract V runReturnObject(ResultSet result) throws SQLException;

    @Override
    public void run(ResultSet result) throws SQLException {
        value = runReturnObject(result);
    }

    public V getValue() {
        return value;
    }
}
