package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import static dataaccess.DatabaseManager.configureDatabase;

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
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("Cannot find user");
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.dropTable("userdata");
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        if(!DatabaseManager.tableExists("userdata")){
            return true;
        }
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
