package me.clcondorcet.itemsorter.database.schemas;

import me.clcondorcet.itemsorter.database.DatabaseManager;
import me.clcondorcet.itemsorter.database.QueryBuilder;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author clcondorcet
 */
public class SystemsTable {

    /*
     * STRUCTURE
     */
    /**
     * @since db_version 1
     */
    public static final String NAME = "Systems";
    /**
     * @since db_version 1
     */
    public static final String COL_SYSTEM_ID = "systemID";
    /**
     * @since db_version 1
     */
    public static final String COL_NAME = "name";
    /**
     * @since db_version 1
     */
    public static final String COL_OWNER_UUID = "ownerUUID";
    /**
     * @since db_version 1
     */
    public static final String COL_OWNER_NAME = "ownerName";
    /**
     * @since db_version 1
     */
    public static final String COL_WORLD = "world";
    /**
     * @since db_version 1
     */
    public static final String COL_BLOCK_X = "x";
    /**
     * @since db_version 1
     */
    public static final String COL_BLOCK_Y = "y";
    /**
     * @since db_version 1
     */
    public static final String COL_BLOCK_Z = "z";
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
    public static final String SELECT_ALL_SYSTEMS
            = "SELECT * FROM `" + NAME + "`";

    /**
     * @since db_version 1
     */
    public static final String INSERT_SYSTEM
            = "INSERT INTO `" + NAME + "` (name, ownerUUID, ownerName, world, x, y, z, signx, signy, signz) VALUES (?,?,?,?,?,?,?,?,?,?)";

    public static int insertSystem(String name, String ownerUUID, String ownerName, String world, int x, int y, int z, int signx, int signy, int signz) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.setAndReturnKey(INSERT_SYSTEM, name, ownerUUID, ownerName, world, x, y, z, signx, signy, signz);
    }

    public static final String UPDATE_OWNER_UUID
            = QueryBuilder
            .updateQuery(NAME)
            .addUpdate(COL_OWNER_UUID)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_SYSTEM_ID)
            .build();

    public static int setOwnerUUID(String newUUID, int systemID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_OWNER_UUID, newUUID, systemID);
    }

    public static final String UPDATE_OWNER_NAME
            = QueryBuilder
            .updateQuery(NAME)
            .addUpdate(COL_OWNER_NAME)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_SYSTEM_ID)
            .build();

    public static int setOwnerName(String newName, int systemID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_OWNER_NAME, newName, systemID);
    }

    public static final String UPDATE_OWNER
            = QueryBuilder
            .updateQuery(NAME)
            .addUpdate(COL_OWNER_NAME)
            .addUpdate(COL_OWNER_UUID)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_SYSTEM_ID)
            .build();

    public static int setOwner(String newName, UUID newUUID, int systemID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String uuidString = null;
        if (newUUID != null) {
            uuidString = newUUID.toString();
        }
        return DatabaseManager.instance.database.set(UPDATE_OWNER, newName, uuidString, systemID);
    }

    /**
     * @since db_version 1
     */
    public static final String DELETE_SYSTEM
            = "DELETE FROM `" + NAME + "` WHERE `" + COL_SYSTEM_ID + "`=?";

    public static int deleteSystem(int systemID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(DELETE_SYSTEM, systemID);
    }
}
