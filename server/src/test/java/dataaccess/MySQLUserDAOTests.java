package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MySQLUserDAOTests {
    MySQLAuthDAO mySQLAuthDAO;
    MySQLUserDAO mySQLUserDAO;
    MySQLGameDAO mySQLGameDAO;

    String testAuthToken;
    final String testUser = "testUser";
    final String password = "password";
    final String email = "example@email.com";

    MySQLUserDAOTests() throws DataAccessException {
        mySQLAuthDAO = new MySQLAuthDAO();
        mySQLGameDAO = new MySQLGameDAO();
        mySQLUserDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLAuthDAO.clear();
        mySQLGameDAO.clear();
        mySQLUserDAO.clear();

        mySQLUserDAO.createUser(testUser, password, email);
        testAuthToken = mySQLAuthDAO.createAuth(testUser);
    }

    @Test
    void createUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void clear() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLUserDAO.clear());
    }

    @Test
    void successIsEmpty() throws DataAccessException {
        mySQLAuthDAO.clear();
        mySQLGameDAO.clear();
        mySQLUserDAO.clear();
        Assertions.assertTrue(mySQLUserDAO.isEmpty(), "Table is not properly cleared");
    }

    @Test
    @DisplayName("Normal is not Empty")
    void successIsNotEmpty() throws DataAccessException {
        Assertions.assertFalse(mySQLUserDAO.isEmpty(), "Table is not properly populated");
    }
}