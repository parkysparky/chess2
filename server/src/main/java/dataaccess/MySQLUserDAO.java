package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySQLUserDAO implements UserDAO{
    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();

    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        if(anyFieldBlank(username, password, email)){
            throw new DataAccessException("bad request");
        }
        try{ //check username is available
            getUser(username);
        }
        catch (DataAccessException e) {//if available, create user
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            var statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";

            DatabaseManager.executeUpdate(statement, username, hashedPassword, email);

            return;
        }
        throw new DataAccessException("already taken");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (anyFieldBlank(username)) { throw new DataAccessException("bad request"); }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM userdata WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        throw new DataAccessException("unauthorized");
    }

    @Override
    public void clear() throws DataAccessException {
        String[] clearUserDataStatements = {
            "SET FOREIGN_KEY_CHECKS = 0;",
            "TRUNCATE TABLE authdata;",
            "TRUNCATE TABLE gamedata;",
            "TRUNCATE TABLE userdata;",
            "SET FOREIGN_KEY_CHECKS = 1;"
        };
        executeUpdates(clearUserDataStatements);
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        var statement = "SELECT COUNT(*) FROM authdata LIMIT 1;";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0; // Returns true if count is 0 (empty table)
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        throw new DataAccessException("Error checking table count");
    }

    private boolean anyFieldBlank(String... params) {
        boolean returnValue = false;
        for(var param : params){
            if(param == null || param.isBlank()) {
                returnValue = true;
                break;
            }
        }

        return returnValue;
    }


}
