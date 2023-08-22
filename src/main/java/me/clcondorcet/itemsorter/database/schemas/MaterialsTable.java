package me.clcondorcet.itemsorter.database.schemas;

import me.clcondorcet.itemsorter.database.DatabaseManager;
import me.clcondorcet.itemsorter.database.QueryBuilder;

import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class MaterialsTable {

    /*
     * STRUCTURE
     */
    /**
     * @since db_version 1
     */
    public static final String NAME = "Materials";
    /**
     * @since db_version 1
     */
    public static final String COL_SYSTEM_ID = SystemsTable.COL_SYSTEM_ID;
    /**
     * @since db_version 1
     */
    public static final String COL_FILTER_ID = FiltersTable.COL_FILTER_ID;
    /**
     * @since db_version 1
     */
    public static final String COL_MATERIAL = "material";
    /**
     * @since db_version 1
     */
    public static final String COL_PRIORITY = "priority";

    /*
     * QUERIES
     */
    /**
     * @since db_version 1
     */
    public static final String SELECT_ALL_MATERIALS
            = "SELECT * FROM `" + NAME + "`";

    /**
     * @since db_version 1
     */
    public static final String INSERT_MATERIAL
            = "INSERT INTO Materials (systemID, filterID, material, priority) VALUES (?,?,?,?)";

    public static int insertMaterial(int systemID, int filterID, String material, int priority) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(INSERT_MATERIAL, systemID, filterID, material, priority);
    }

    public static final String UPDATE_PRIORITY
            = QueryBuilder.updateQuery(NAME)
            .addUpdate(COL_PRIORITY)
            .setConditionType(QueryBuilder.ConditionType.AND)
            .addCondition(COL_SYSTEM_ID)
            .addCondition(COL_FILTER_ID)
            .addCondition(COL_MATERIAL)
            .build();

    public static int updatePriority(int systemID, int filterID, String material, int priority) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(UPDATE_PRIORITY, priority, systemID, filterID, material);
    }

    /**
     * @since db_version 1
     */
    public static final String DELETE_MATERIAL
            = "DELETE FROM `" + NAME + "` WHERE `" + COL_SYSTEM_ID + "`=? AND `" + COL_FILTER_ID + "`=? AND `" + COL_MATERIAL + "`=?";

    public static int deleteMaterial(int systemID, int filterID, String material) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return DatabaseManager.instance.database.set(DELETE_MATERIAL, systemID, filterID, material);
    }
}
