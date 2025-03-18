package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

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
    void successCreateUser() throws DataAccessException {//depends on clear and get
        Assertions.assertNotNull(mySQLAuthDAO.getAuth(testAuthToken), "Token in database is null");
        Assertions.assertEquals(testAuthToken, mySQLAuthDAO.getAuth(testAuthToken).authToken(),
                "Authtoken stored in database does not equal generated auth token");
    }

    @Test
    void createUserBlankUsername() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(" ", password, email),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void createUserBlankPassword() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(testUser, " ", email),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void createUserBlankEmail() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(testUser, password, " "),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void createUserNullUsername() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(null, password, email),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void createUserNullPassword() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(testUser, null, email),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void createUserNullEmail() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(testUser, password, null),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void createUserUsernameTaken() {
        Assertions.assertThrows(DataAccessException.class,
                () -> mySQLUserDAO.createUser(testUser, password, email),
                "Expected DataAccessException \"unauthorized\"");
    }

    @Test
    void successGetUser() throws DataAccessException {
        UserData foundUser = mySQLUserDAO.getUser(testUser);

        Assertions.assertEquals(testUser, foundUser.username(),
                "fetched username does not match expected username");
        Assertions.assertTrue(BCrypt.checkpw(password, foundUser.password()),
                "fetched hashedPassword does not match expected hashedPassword");
        Assertions.assertEquals(email, foundUser.email(),
                "fetched email does not match expected email");
    }

    @Test
    void getInvalidUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLUserDAO.getUser("badUsername"),
                "Expected DataAccessException \"unauthorized\"");
    }

    @Test
    void getNullUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLUserDAO.getUser(null),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void getBlankUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> mySQLUserDAO.getUser(" "),
                "Expected DataAccessException \"bad request\"");
    }

    @Test
    void clear() throws DataAccessException {
        mySQLAuthDAO.clear();
        mySQLGameDAO.clear();
        mySQLUserDAO.clear();
        Assertions.assertTrue(mySQLUserDAO.isEmpty(), "Table is not properly cleared");
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