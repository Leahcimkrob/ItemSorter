package me.clcondorcet.itemsorter.database.schemas;

/**
 * @author clcondorcet
 */
public class ItemSorterDBTable {

    /*
     * STRUCTURE
     */
    /**
     * @since db_version 1
     */
    public static final String NAME = "ItemSorterDB";
    /**
     * @since db_version 1
     */
    public static final String COL_SCHEMA_VERSION = "schema_version";

    /*
     * QUERIES
     */
    /**
     * @since db_version 1
     */
    public static final String GET_DB_VERSION
            = "SELECT schema_version FROM ItemSorterDB ORDER BY schema_version DESC LIMIT 1";
    /**
     * @since db_version 1
     */
    public static final String SET_DB_VERSION
            = "INSERT INTO ItemSorterDB (schema_version) values (?)";
}
