package me.clcondorcet.itemsorter.database.schemas;

import me.clcondorcet.itemsorter.database.DatabaseManager;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class DepositsTable {

    /*
     * STRUCTURE
     */
    /**
     * @since db_version 1
     */
    public static final String NAME = "Deposits";
    /**
     * @since db_version 1
     */
    public static final String COL_DEPOSIT_ID = "depositID";
    /**
     * @since db_version 1
     */
    public static final String COL_SYSTEM_ID = SystemsTable.COL_SYSTEM_ID;
    /**
     * @since db_version 1
     */
    public static final String COL_WORLD = "world";
    /**
     * @since db_version 1
     */
    public static final String COL_X = "x";
    /**
     * @since db_version 1
     */
    public static final String COL_Y = "y";
    /**
     * @since db_version 1
     */
    public static final String COL_Z = "z";
    /**
     * @since db_version 1
     */
    public static final String COL_SIGN_X = "signx";
    /**
     * @since db_version 1
     */
    public static final String COL_SIGN_Y = "signy";
    /**
     * @since db_version 1
     */
    public static final String COL_SIGN_Z = "signz";


    /*
     * QUERIES
     */
    /**
     * @since db_version 1
     */
    public static final String SELECT_ALL_DEPOSITS
            = "SELECT * FROM `" + NAME + "`";

    /**
     * @since db_version 1
     */
    public static final String INSERT_DEPOSIT
            = "INSERT INTO Deposits (systemID, world, x, y, z, signx, signy, signz) VALUES (?,?,?,?,?,?,?,?)";

    public static int insertDeposit(int systemID, String world, int x, int y, int z, int signx, int signy, int signz) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.setAndReturnKey(INSERT_DEPOSIT, systemID, world, x, y, z, signx, signy, signz);
    }

    /**
     * @since db_version 1
     */
    public static final String DELETE_DEPOSIT
            = "DELETE FROM `" + NAME + "` WHERE `" + COL_DEPOSIT_ID + "`=?";

    public static int deleteDeposit(int depositID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(DELETE_DEPOSIT, depositID);
    }
}
