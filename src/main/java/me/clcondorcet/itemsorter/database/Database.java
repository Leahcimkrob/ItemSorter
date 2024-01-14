package me.clcondorcet.itemsorter.database;

import me.clcondorcet.itemsorter.ItemSorter;
import me.clcondorcet.itemsorter.multiversion.VersionHandler;
import org.bukkit.entity.Item;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;

/**
 * @author clcondorcet
 */
public class Database {
    private static final String SQLITE_LIB_NAME = "sqlite-jdbc-3.40.1.0.jar";
    private static final String SQLITE_LIB_URL = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.40.1.0/sqlite-jdbc-3.40.1.0.jar";

    private final String url;
    private final DatabaseType type;
    private String databaseName;
    private boolean default_lib = true;

    public static boolean canReturnKey = !VersionHandler.isVersionSupOrEqualThan("1_20_2");

    public Database(String Host, String db, String username, String password) {
        databaseName = db;
        url = "jdbc:mysql://" + Host + "/" + db + "?user=" + username + "&password=" + password;
        type = DatabaseType.MYSQL;
    }

    public Database(String filePath) {
        url = "jdbc:sqlite:" + new File(filePath).getAbsolutePath();
        type = DatabaseType.SQLITE;
        if (ItemSorter.configManager.getConfig("config.yml").getBoolean("customsqlitelib")) {
            File file = new File(new File(ItemSorter.getInstance().getDataFolder(), ItemSorter.LIB_FOLDER), SQLITE_LIB_NAME);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if(!file.exists()){
                try (BufferedInputStream in = new BufferedInputStream(new URL(SQLITE_LIB_URL).openStream());
                     OutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    default_lib = false;
                } catch (IOException e) {
                    ItemSorter.getInstance().getLogger().severe("Error when downloading sqlite library. Using default one as backup (may not work on your server). You can also download the library by hand here: " + SQLITE_LIB_URL + " and past it here: " + file.getParentFile().getAbsolutePath());
                }
            } else {
                default_lib = false;
            }
        } else {
            default_lib = true;
        }
    }

    public DatabaseType getType() {
        return type;
    }

    public String getDriver() {
        return type.DRIVER_CLASS;
    }

    public Connection open() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            switch (type){
                case MYSQL:
                    Class.forName(this.getDriver());
                    return DriverManager.getConnection(url);
                case SQLITE:
                    if (default_lib) {
                        Class.forName(this.getDriver());
                        return DriverManager.getConnection(url);
                    } else {
                        File library_folder = new File(ItemSorter.getInstance().getDataFolder(), ItemSorter.LIB_FOLDER);
                        URL[] urls = new URL[] {new File(library_folder, SQLITE_LIB_NAME).toURI().toURL()};
                        URLClassLoader classLoader = new URLClassLoader(urls);
                        System.setProperty("org.sqlite.tmpdir", library_folder.getAbsolutePath());
                        Class<?> jdbcClass = classLoader.loadClass("org.sqlite.JDBC");
                        Object jdbcObj = jdbcClass.newInstance();
                        return (Connection) jdbcClass.getMethod("connect", String.class, Properties.class).invoke(jdbcObj, url, new Properties());//jdbc.connect(url, new Properties());
                    }
                default:
                    throw new SQLException("Driver not found");
            }
        } catch (SQLException e) {
            ItemSorter.getInstance().getLogger().severe("Could not connect to MySQL/SQLite server! The error:");
            throw e;
        } catch (ClassNotFoundException e) {
            ItemSorter.getInstance().getLogger().severe("JDBC Driver not found!");
            throw e;
        } catch (InstantiationException | IllegalAccessException e) {
            ItemSorter.getInstance().getLogger().severe("JDBC driver cannot load.");
            throw e;
        } catch (InvocationTargetException | NoSuchMethodException | MalformedURLException e) {
            e.printStackTrace();
            throw new SQLException(e.getCause());
        }
    }

    public void get(String query, ResultCallBack callBack, Object... arguments) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query)) {
            QueryVariables vars = new QueryVariables(arguments);
            vars.setValues(stmt);
            ResultSet res = stmt.executeQuery();
            callBack.run(res);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void get(String query, ResultCallBack callBack, QueryVariables variables) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query)) {
            variables.setValues(stmt);
            ResultSet res = stmt.executeQuery();
            callBack.run(res);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void get(String query, ResultCallBack callBack) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query)) {
            ResultSet res = stmt.executeQuery();
            callBack.run(res);
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int set(String query, Object... variables) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query)) {
            QueryVariables vars = new QueryVariables(variables);
            vars.setValues(stmt);
            return stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int set(String query, QueryVariables variables) {
        try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query)) {
            variables.setValues(stmt);
            return stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int set(String query) {
        try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query)) {
            return stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int setAndReturnKey(String query, Object... variables) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (canReturnKey) {
            try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                QueryVariables vars = new QueryVariables(variables);
                vars.setValues(stmt);
                stmt.executeUpdate();
                ResultSet res = stmt.getGeneratedKeys();
                res.next();
                return res.getInt(1);
            } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            try (Connection con = this.open(); PreparedStatement stmt = con.prepareStatement(query + " RETURNING rowid")) {
                QueryVariables vars = new QueryVariables(variables);
                vars.setValues(stmt);
                ResultSet res = stmt.executeQuery();
                res.next();
                return res.getInt(1);
            } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Prepare a update statement, automatically set values and run the update.
     * @param con The database connexion
     * @param query The query to execute
     * @param vars The variables of the query
     * @return The last inserted ID (AUTO_INCREMENT)
     */
    public int setAndReturnKey(Connection con, String query, Object... vars) throws SQLException {
        if (canReturnKey) {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            QueryVariables var = new QueryVariables(vars);
            var.setValues(stmt);
            stmt.executeUpdate();
            ResultSet res = stmt.getGeneratedKeys();
            res.next();
            return res.getInt(1);
        } else {
            PreparedStatement stmt = con.prepareStatement(query + " RETURNING rowid");
            QueryVariables var = new QueryVariables(vars);
            var.setValues(stmt);
            ResultSet res = stmt.executeQuery();
            res.next();
            return res.getInt(1);
        }
    }

    public long set(Connection con, String query, Object... vars) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(query);
        QueryVariables var = new QueryVariables(vars);
        var.setValues(stmt);
        return stmt.executeUpdate();
    }

    public boolean isTable(final String table) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String query = null;
        QueryVariables vars = null;
        switch (type) {
            case MYSQL:
                query = "SELECT \n" +
                        "   TABLE_SCHEMA, \n" +
                        "   TABLE_NAME,\n" +
                        "   TABLE_TYPE\n" +
                        "FROM \n" +
                        "   information_schema.TABLES \n" +
                        "WHERE \n" +
                        "   TABLE_SCHEMA LIKE ? AND \n" +
                        "\tTABLE_TYPE LIKE 'BASE TABLE' AND\n" +
                        "\tTABLE_NAME = ?;";
                vars = new QueryVariables(this.databaseName, table);
                break;
            case SQLITE:
                query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
                vars = new QueryVariables(table);
        }
        ObjectResultCallBack<Boolean> callBack = new ObjectResultCallBack<Boolean>() {
            @Override
            public Boolean runReturnObject (ResultSet result) throws SQLException {
                try {
                    return result.next();
                } catch (SQLException e) {
                    throw e;
                }
            }
        };
        get(query, callBack, vars);
        return callBack.getValue();
    }

    enum DatabaseType {
        SQLITE("org.sqlite.JDBC"),
        MYSQL("com.mysql.jdbc.Driver");

        public final String DRIVER_CLASS;

        DatabaseType (String driverClass) {
            this.DRIVER_CLASS = driverClass;
        }
    }
}