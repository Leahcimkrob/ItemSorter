package me.clcondorcet.itemsorter.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author clcondorcet
 */
public class QueryVariables {

    private final QueryVariable[] variables;

    public QueryVariables(Object... variables) {
        this.variables = new QueryVariable[variables.length];
        int i = 0;
        for (Object var : variables) {
            this.variables[i++] = new QueryVariable(var);
        }
    }

    public QueryVariables (QueryVariable[] variables) {
        this.variables = variables.clone();
    }

    public void setValues(PreparedStatement stmt) throws SQLException {
        int i = 0;
        for (QueryVariable var : variables) {
            var.setValue(++i, stmt);
        }
    }
}