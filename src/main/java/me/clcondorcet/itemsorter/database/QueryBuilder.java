package me.clcondorcet.itemsorter.database;

import java.util.LinkedList;

/**
 * @author clcondorcet
 */
public class QueryBuilder {

    public static UpdateQuery updateQuery(String tableName) {
        return new UpdateQuery(tableName);
    }

    /**
     * @author clcondorcet
     */
    public static class UpdateQuery {

        private String tableName;
        private final LinkedList<ColumnWithValue> updatedColumns = new LinkedList<>();
        private final LinkedList<ColumnWithValue> conditions = new LinkedList<>();
        private ConditionType conditionType = ConditionType.AND;

        UpdateQuery(String tableName) {
            this.tableName = tableName;
        }

        public UpdateQuery setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public UpdateQuery addUpdate(ColumnWithValue cwv) {
            updatedColumns.add(cwv);
            return this;
        }

        public UpdateQuery addUpdate(String columnName) {
            updatedColumns.add(new ColumnWithValue(columnName));
            return this;
        }

        public UpdateQuery addUpdate(String columnName, Object value) {
            updatedColumns.add(new ColumnWithValue(columnName, value));
            return this;
        }

        public UpdateQuery addCondition(ColumnWithValue cwv) {
            conditions.add(cwv);
            return this;
        }

        public UpdateQuery addCondition(String columnName) {
            conditions.add(new ColumnWithValue(columnName));
            return this;
        }

        public UpdateQuery addCondition(String columnName, Object value) {
            conditions.add(new ColumnWithValue(columnName, value));
            return this;
        }

        public UpdateQuery setConditionType(ConditionType conditionType) {
            this.conditionType = conditionType;
            return this;
        }

        public String build() {
            if (updatedColumns.isEmpty()) throw new IllegalArgumentException("Query builder cannot build empty update query");
            StringBuilder query = new StringBuilder("UPDATE `" + tableName + "` SET ");
            int i = 0;
            for (ColumnWithValue cwv : updatedColumns) {
                if (i++ != 0) {
                    query.append(", ");
                }
                query.append(cwv.build());
            }
            if (!conditions.isEmpty()) {
                query.append(" WHERE ");
                i = 0;
                for (ColumnWithValue cond : conditions) {
                    if (i++ != 0) {
                        query.append(" ").append(conditionType.name()).append(" ");
                    }
                    query.append(cond.build());
                }
            }
            return query.toString();
        }
    }

    /**
     * @author clcondorcet
     */
    private static class ColumnWithValue {

        private static class SQLVariable {}
        private static final SQLVariable SQL_VAR = new SQLVariable();

        private final ObjectType type;
        private final String columnName;
        private final Object value;

        ColumnWithValue (String columnName) {
            this.columnName = columnName;
            this.type = null;
            this.value = SQL_VAR;
        }

        ColumnWithValue (String columnName, Object value) {
            this.columnName = columnName;
            this.type = ObjectType.getFromObject(value);
            this.value = value;
        }

        public String build() {
            String built = "`" + columnName + "`=";
            if (value == SQL_VAR) {
                built += "?";
            } else {
                switch (type) {
                    case LOGICAL:
                    case STRING:
                        built += "'" + value.toString() + "'";
                        break;
                    case INTEGER:
                    case BIGINT:
                        built += value.toString();
                        break;
                    default:
                        throw new IllegalArgumentException("Cannot build this column because we don't know how to build this value type: " + ObjectType.OBJECT + " (" + value.getClass().getName() + ")");
                }
            }
            return built;
        }
    }

    /**
     * @author clcondorcet
     */
    public enum ConditionType {
        OR,
        AND
    }
}
