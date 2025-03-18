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
        try{ //check username is available
            getUser(username);
        }
        catch (DataAccessException e) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            var statement = "INSERT INTO userdata (username, hashedPassword, email) VALUES (?, ?, ?)";

            DatabaseManager.executeUpdate(statement, username, hashedPassword, email);

        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return true;
    }


}
