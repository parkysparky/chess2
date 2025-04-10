package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLAuthDAOTests {
    MySQLAuthDAO mySQLAuthDAO;
    MySQLUserDAO mySQLUserDAO;

    String testAuthToken;
    final String testUser = "testUser";
    final String password = "password";
    final String email = "example@email.com";

    MySQLAuthDAOTests() throws DataAccessException {
        mySQLAuthDAO = new MySQLAuthDAO();
        mySQLUserDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLAuthDAO.clear();
        mySQLUserDAO.clear();

        mySQLUserDAO.createUser(testUser, password, email);
        testAuthToken = mySQLAuthDAO.createAuth(testUser);
    }

    @Test
    void successCreateAuth() throws DataAccessException {//depends on clear and get
        Assertions.assertNotNull(mySQLAuthDAO.getAuth(testAuthToken), "Token in database is null");
        Assertions.assertEquals(testAuthToken, mySQLAuthDAO.getAuth(testAuthToken).authToken(),
                "Authtoken stored in database does not equal generated auth token");
    }

    @Test
    void createAuthNoToken() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLAuthDAO.createAuth(" "), "Expected DataAccessException \"unauthorized\"");
    }

    @Test
    void successGetAuth() throws DataAccessException {
        AuthData foundAuth = mySQLAuthDAO.getAuth(testAuthToken);

        Assertions.assertEquals(testUser, foundAuth.username(), "fetched username for AuthData, does not match expected username");
        Assertions.assertEquals(testAuthToken, foundAuth.authToken(), "fetched authToken for AuthData, does not match expected authToken");
    }

    @Test
    void getInvalidAuth() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLAuthDAO.getAuth("badToken"),
                "Expected DataAccessException \"unauthorized\"");
    }

    @Test
    void successDeleteAuth() throws DataAccessException {
        mySQLAuthDAO.deleteAuth(new AuthData(testAuthToken, testUser));
        Assertions.assertTrue(mySQLAuthDAO.isEmpty(), "Record is not properly deleted");
    }

    @Test
    void deleteAuthBadInput() {
        Assertions.assertThrows(DataAccessException.class,
                () -> {mySQLAuthDAO.deleteAuth(new AuthData("badAuthToken", testUser));},
                "Expected DataAccessException \"unauthorized\"");
    }

    @Test
    void clear() throws DataAccessException {
        mySQLAuthDAO.clear();
        Assertions.assertTrue(mySQLAuthDAO.isEmpty(), "Table is not properly cleared");
    }

    @Test
    void successIsEmpty() throws DataAccessException {
        mySQLAuthDAO.clear();
        Assertions.assertTrue(mySQLAuthDAO.isEmpty(), "Table is not properly cleared");
    }

    @Test
    @DisplayName("Normal is not Empty")
    void successIsNotEmpty() throws DataAccessException {
        Assertions.assertFalse(mySQLAuthDAO.isEmpty(), "Table is not properly populated");
    }
}