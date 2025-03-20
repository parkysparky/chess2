package dataaccess;

import model.AuthData;

import java.util.UUID;

import static dataaccess.DatabaseManager.configureDatabase;
import static dataaccess.DatabaseManager.executeUpdate;

public class MySQLAuthDAO implements AuthDAO{

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        //input validation
        if(anyFieldBlank(username)){ throw new DataAccessException("Some required fields are missing"); }

        //generate auth token
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);

        var statement = "INSERT INTO authdata (authToken, username) VALUES (?, ?)";

        DatabaseManager.executeUpdate(statement, token, username);

        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        //input validation
        if(anyFieldBlank(authToken)){ throw new DataAccessException("Some required fields are missing"); }

        var statement = "SELECT * FROM authdata WHERE authToken=?";


        try (var conn = DatabaseManager.getConnection()) {
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
    public void deleteAuth(AuthData authData) throws DataAccessException {
        //input validation
        if(anyFieldBlank(authData)){ throw new DataAccessException("Some required fields are missing"); }

        String authToken = authData.authToken();
        var statement = "DELETE FROM authdata WHERE authToken = ?";

        if ( DatabaseManager.executeUpdate(statement, authToken) == 0 ){
            throw new DataAccessException("unauthorized");
        }

    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE authdata";
        DatabaseManager.executeUpdate(statement);
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

    private boolean anyFieldBlank(Object... params) {
        boolean hasBlankField = false;
        for(var param : params){
            if (param == null){
                hasBlankField = true;
                break;
            }
            switch(param) {
                case String s -> { if (s.isBlank()) { hasBlankField = true; } }
                case AuthData a -> {hasBlankField = anyFieldBlank(a.authToken(), a.username());}
                default -> {  }
                }
            if(hasBlankField) {break;}
        }
        return hasBlankField;
    }

}
