package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData username) throws DataAccessException;
    void clear() throws DataAccessException;
    boolean isEmpty();
}
