package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
}
