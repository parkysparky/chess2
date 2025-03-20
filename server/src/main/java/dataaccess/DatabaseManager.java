package dataaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates the tables if they do not already exist.
     */
    static public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createTableStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    static private final String[] createTableStatements = {
        //might need the following at the end of each table. I'm not sure
        //ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
        CREATE TABLE IF NOT EXISTS  userdata (
          `username` varchar(64) NOT NULL,
          `password` varchar(64) NOT NULL,
          `email` varchar(128) NOT NULL,
          PRIMARY KEY (`username`))
          ENGINE=InnoDB
        """,
        """
        CREATE TABLE IF NOT EXISTS  authdata (
          `authToken` varchar(64) NOT NULL,
          `username` varchar(64) NOT NULL,
          PRIMARY KEY (`authToken`),
          FOREIGN KEY(`username`) REFERENCES UserData(username) ON DELETE CASCADE)
          ENGINE=InnoDB
        """,
        """
        CREATE TABLE IF NOT EXISTS  gamedata (
          `gameID` INT NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(64),
          `blackUsername` varchar(64),
          `gameName` varchar(64) NOT NULL,
          `game` TEXT NOT NULL,
          PRIMARY KEY (`gameID`),
          FOREIGN KEY (`whiteUsername`) REFERENCES UserData(username) ON DELETE CASCADE,
          FOREIGN KEY(`blackUsername`) REFERENCES UserData(username) ON DELETE CASCADE)
          ENGINE=InnoDB
        """
    };


    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Executes an arbitrary number of prepared SQL statements
     */
    static public void executeUpdates(String... statements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    /**
     * Prepares and executes an update SQL statement with an arbitrary number of parameters.
     */
    static int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                if(params != null) {
                    for (var i = 0; i < params.length; i++) {
                        var param = params[i];
                        switch (param) {
                            case String p -> ps.setString(i + 1, p);
                            case Integer p -> ps.setInt(i + 1, p);
                            case null -> ps.setNull(i + 1, NULL);
                            default -> {
                            }
                        }
                    }
                }
                int retVal = ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) { //if we generate a key, return it
                    retVal = rs.getInt(1);
                }

                return retVal; //otherwise return the number of rows updated
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    /**
     * Prepares and executes a get SQL statement with an arbitrary number of parameters
     * ResultSetMapper is a functional interface. To write this parameter, use a lambda expression.
     * The lambda should be in the following form: resultSet -> (element of list)
     * generate the element of the list as necessary from the resultSet data members
     * ex. rs -> ( new UserData(rs.getString("username"), rs.getString("password"),  rs.getString("email")) )
     */
    static <T> List<T> executeQuery(String statement, ResultSetMapper<T> mapper, Object... params) throws DataAccessException {
        List<T> results = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, Types.NULL);
                        default -> throw new IllegalArgumentException("Unsupported parameter type: " + param.getClass().getSimpleName());
                    }
                }

                try(var resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        results.add(mapper.map(resultSet));
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }

        return results;
    }

}
