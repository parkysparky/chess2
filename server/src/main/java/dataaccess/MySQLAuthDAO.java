package dataaccess;

import model.AuthData;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLAuthDAO implements AuthDAO{

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData username) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
