package dataaccess;

import model.AuthData;

import java.util.List;
import java.util.UUID;

import static dataaccess.DatabaseManager.*;

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

        //generate list of data returned by query
        var statement = "SELECT * FROM authdata WHERE authToken = ?";
        List<AuthData> authDataList = executeQuery(statement,
                                            rs -> ( new AuthData(authToken,
                                                                        rs.getString("username")) ),
                                                    authToken);

        if(authDataList.isEmpty()){
            throw new DataAccessException("unauthorized");
        }

        //return AuthData if found
        return authDataList.getFirst();
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
        //count number of entries in authData, LIMIT 1 means if any entries stop, not empty
        List<Integer> authCount = executeQuery(statement,
                rs -> (rs.getInt(1)) );

        //return whether any entries were counted. if count == 0 then isEmpty = true
        return authCount.getFirst() == 0;
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
