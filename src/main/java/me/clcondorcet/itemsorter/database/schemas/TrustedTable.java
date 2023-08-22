package me.clcondorcet.itemsorter.database.schemas;

import me.clcondorcet.itemsorter.database.DatabaseManager;
import me.clcondorcet.itemsorter.database.QueryBuilder;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class TrustedTable {

    /*
     * STRUCTURE
     */
    /**
     * @since db_version 1
     */
    public static final String NAME = "Trusted";
    /**
     * @since db_version 1
     */
    public static final String COL_TRUSTED_ID = "trustedID";
    /**
     * @since db_version 1
     */
    public static final String COL_SYSTEM_ID = SystemsTable.COL_SYSTEM_ID;
    /**
     * @since db_version 1
     */
    public static final String COL_TRUSTED_UUID = "trustedUUID";
    /**
     * @since db_version 1
     */
    public static final String COL_TRUSTED_NAME = "trustedName";

    /*
     * QUERIES
     */
    /**
     * @since db_version 1
     */
    public static final String SELECT_ALL_TRUSTED
            = "SELECT * FROM `" + NAME + "`";

    /**
     * @since db_version 1
     */
    public static final String INSERT_TRUSTED
            = "INSERT INTO `" + NAME + "` (systemID, trustedUUID, trustedName) VALUES (?,?,?)";

    public static int insertTrusted(int systemID, String trustedUUID, String trustedName) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.setAndReturnKey(INSERT_TRUSTED, systemID, trustedUUID, trustedName);
    }

    public static final String UPDATE_UUID
            = QueryBuilder
                .updateQuery(NAME)
                .addUpdate(COL_TRUSTED_UUID)
                .setConditionType(QueryBuilder.ConditionType.AND)
                .addCondition(COL_TRUSTED_ID)
                .build();

    public static int setUUID(String newUUID, int trustedID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_UUID, newUUID, trustedID);
    }

    public static final String UPDATE_NAME
            = QueryBuilder
            .updateQuery(NAME)
            .addUpdate(COL_TRUSTED_NAME)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_TRUSTED_ID)
            .build();

    public static int setName(String newName, int trustedID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_NAME, newName, trustedID);
    }

    /**
     * @since db_version 1
     */
    public static final String DELETE_TRUSTED
            = "DELETE FROM `" + NAME + "` WHERE `" + COL_TRUSTED_ID + "`=?";

    public static int deleteTrusted(int trustedID) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(DELETE_TRUSTED, trustedID);
    }
}
