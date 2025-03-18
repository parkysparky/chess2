package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLAuthDAO implements AuthDAO{

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        //generate auth token
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);

        var statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";

        DatabaseManager.executeUpdate(statement, token, username);

        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM authdata WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        throw new DataAccessException("unauthorized");
    }

    @Override
    public void deleteAuth(AuthData username) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DROP TABLE authdata;";

        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
