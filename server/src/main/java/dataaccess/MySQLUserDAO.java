package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

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

        var statement = "SELECT * FROM userdata WHERE username=?";
        List<UserData> userDataList = executeQuery(statement,
                                            rs -> ( new UserData(rs.getString("username"),
                                                                        rs.getString("password"),
                                                                        rs.getString("email")) ),
                                                    username);

        if(userDataList.isEmpty()){//this condition might be wrong
            throw new DataAccessException("User Not Found");
        }

        return userDataList.getFirst();
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

        //count number of entries in authData, LIMIT 1 means if any entries stop, not empty
        List<Integer> authCount = executeQuery(statement,
                rs -> (rs.getInt(1)) );

        //return whether any entries were counted. if count == 0 then isEmpty = true
        return authCount.getFirst() == 0;
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
