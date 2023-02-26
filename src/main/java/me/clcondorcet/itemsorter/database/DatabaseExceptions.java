package me.clcondorcet.itemsorter.database;

/**
 * @author clcondorcet
 */
public class DatabaseExceptions {

    /**
     * @author clcondorcet
     */
    public static class DatabaseException extends RuntimeException {
        DatabaseException() { super(); }
        DatabaseException(String message) { super(message); }
    }

    /**
     * @author clcondorcet
     */
    public static class ArgumentTypeInvalidException extends DatabaseException {
        ArgumentTypeInvalidException() { super(); }
        ArgumentTypeInvalidException(String message) { super(message); }
    }

}
