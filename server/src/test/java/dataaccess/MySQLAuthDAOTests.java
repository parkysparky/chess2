package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLAuthDAOTests {
    MySQLAuthDAO mySQLAuthDAO;

    String testAuthToken;
    final String testUser = "testUser";
    MySQLAuthDAOTests() throws DataAccessException {
        mySQLAuthDAO  = new MySQLAuthDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLAuthDAO.clear();

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

    }

    @Test
    void successGetAuth() throws DataAccessException {
        AuthData foundAuth = mySQLAuthDAO.getAuth(testAuthToken);

        Assertions.assertEquals(testUser, foundAuth.username(), "fetched username for AuthData, does not match expected username");
        Assertions.assertEquals(testUser, foundAuth.username(), "fetched authToken for AuthData, does not match expected authToken");
    }

    @Test
    void getInvalidAuth(){
        Assertions.assertThrows(DataAccessException.class, () -> mySQLAuthDAO.getAuth(testAuthToken),
                "Expected DataAccessException \"unauthorized\"");
    }

    @Test
    void successDeleteAuth() {
    }

    @Test
    void clear() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLAuthDAO.clear());
    }

    @Test
    void isEmpty() {
    }
}