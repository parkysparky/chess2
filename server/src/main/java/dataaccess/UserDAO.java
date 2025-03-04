package dataaccess;

public interface UserDAO {
    void createUser(String username, String password, String email) throws DataAccessException;
    String getUser(String username) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;

}
