package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySQLUserDAOTests {
    MySQLUserDAO mySQLUserDAO;

    final String testUser = "testUser";
    final String password = "password";
    final String email = "example@email.com";

    MySQLUserDAOTests() throws DataAccessException {
        mySQLUserDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLUserDAO.clear();

        mySQLUserDAO.createUser(testUser, password, email);

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
    void isEmpty() {
    }
}