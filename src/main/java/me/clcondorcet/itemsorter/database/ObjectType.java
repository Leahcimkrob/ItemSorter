package me.clcondorcet.itemsorter.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author clcondorcet
 * @see <a href="https://infocenter.sybase.com/help/index.jsp?topic=/com.sybase.infocenter.dc31652.1550/html/java/BABJBDHD.htm">Datatype mapping between Java and SQL</a>
 */
public enum ObjectType {
    STRING,
    INTEGER,
    BIGINT,
    OBJECT,
    LOGICAL
    ;

    void setValue(int index, PreparedStatement stmt, Object value) throws SQLException {
        switch (this) {
            case STRING:
                assert value instanceof String;
                stmt.setString(index, (String) value);
                break;
            case INTEGER:
                assert Integer.class.isInstance(value); // Allow .class.isInstance allow primitive types instanceof
                stmt.setInt(index, (Integer) value);
                break;
            case BIGINT:
                assert Long.class.isInstance(value); // Allow .class.isInstance allow primitive types instanceof
                stmt.setLong(index, (Long) value);
                break;
            case LOGICAL:
                assert Boolean.class.isInstance(value); // Allow .class.isInstance allow primitive types instanceof
                stmt.setBoolean(index, (Boolean) value);
                break;
            case OBJECT:
            default:
                stmt.setObject(index, value);
        }
    }

    static ObjectType getFromObject(Object value) {
        if (value instanceof String) {
            return STRING;
        } else if (Integer.class.isInstance(value)) { // Allow .class.isInstance allow primitive types instanceof
            return INTEGER;
        } else if (Long.class.isInstance(value)) { // Allow .class.isInstance allow primitive types instanceof
            return BIGINT;
        } else if (Boolean.class.isInstance(value)) { // Allow .class.isInstance allow primitive types instanceof
            return LOGICAL;
        } else {
            return OBJECT;
        }
    }
}