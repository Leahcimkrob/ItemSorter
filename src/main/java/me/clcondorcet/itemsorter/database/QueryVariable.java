package me.clcondorcet.itemsorter.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author clcondorcet
 */
class QueryVariable {

    private final ObjectType type;
    private final Object value;

    QueryVariable (Object value) {
        this.type = ObjectType.getFromObject(value);
        this.value = value;
    }

    QueryVariable (Object value, ObjectType type) {
        this.value = value;
        this.type = type;
    }

    public ObjectType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(int index, PreparedStatement stmt) throws SQLException {
        this.type.setValue(index, stmt, this.getValue());
    }
}