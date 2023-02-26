package me.clcondorcet.itemsorter.database.schemas;

import me.clcondorcet.itemsorter.database.DatabaseManager;
import me.clcondorcet.itemsorter.database.QueryBuilder;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class FiltersTable {

    /*
     * STRUCTURE
     */
    /**
     * @since db_version 1
     */
    public static final String NAME = "Filters";
    /**
     * @since db_version 1
     */
    public static final String COL_FILTER_ID = "filterID";
    /**
     * @since db_version 1
     */
    public static final String COL_SYSTEM_ID = SystemsTable.COL_SYSTEM_ID;
    /**
     * @since db_version 1
     */
    public static final String COL_IS_TRASH = "trash";
    /**
     * @since db_version 1
     */
    public static final String COL_TRASH_PRIORITY = "trashPriority";
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
    public static final String SELECT_ALL_FILTERS
            = "SELECT * FROM `" + NAME + "`";

    /**
     * @since db_version 1
     */
    public static final String INSERT_FILTER
            = "INSERT INTO Filters (systemID, trash, trashPriority, world, x, y, z, signx, signy, signz) VALUES (?,?,?,?,?,?,?,?,?,?)";

    public static int insertFilter(int systemID, boolean trash, int trashPriority, String world, int x, int y, int z, int signx, int signy, int signz) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.setAndReturnKey(INSERT_FILTER, systemID, trash, trashPriority, world, x, y, z, signx, signy,  signz);
    }

    public static final String UPDATE_IS_TRASH
            = QueryBuilder.updateQuery(NAME)
            .addUpdate(COL_IS_TRASH)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_FILTER_ID)
            .build();

    public static int updateIsTrash(int filterID, boolean newValue) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_IS_TRASH, newValue, filterID);
    }

    public static final String UPDATE_TRASH_PRIORITY
            = QueryBuilder.updateQuery(NAME)
            .addUpdate(COL_TRASH_PRIORITY)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_FILTER_ID)
            .build();

    public static int updateTrashPriority(int filterID, int newTrashPriority) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_TRASH_PRIORITY, newTrashPriority, filterID);
    }

    public static final String UPDATE_TRASH_PRIORITY_AND_IS_TRASH
            = QueryBuilder.updateQuery(NAME)
            .addUpdate(COL_IS_TRASH)
            .addUpdate(COL_TRASH_PRIORITY)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_FILTER_ID)
            .build();

    public static int updateTrashPriorityAndIsTrash(int filterID, int newTrashPriority, boolean isTrash) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_TRASH_PRIORITY_AND_IS_TRASH, isTrash, newTrashPriority, filterID);
    }

    /**
     * @since db_version 1
     */
    public static final String DELETE_FILTER
            = "DELETE FROM `" + NAME + "` WHERE `" + COL_FILTER_ID + "`=?";

    public static int deleteFilter(int filterID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(DELETE_FILTER, filterID);
    }
}
