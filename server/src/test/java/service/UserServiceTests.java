package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.DataInputException;
import server.service.UserService;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import server.service.result.LoginResult;
import server.service.result.LogoutResult;
import server.service.result.RegisterResult;

class UserServiceTests {
    /// add a case for incomplete input for each endpoint
    /// create a class for it maybe? check how it is handled in the api tests

    UserService userService = new UserService();

    String testAuthToken;
    final String testUser = "testUser";
    final String password = "password";
    final String email = "example@email.com";

    UserServiceTests() throws DataAccessException {
    }

    @BeforeEach
    void setUp() throws DataInputException{
        //reset database
        userService.clear();

        //get authToken for sample user
        RegisterRequest registerRequest = new RegisterRequest(testUser, password, email);
        RegisterResult registerResult = userService.register(registerRequest);
        testAuthToken = registerResult.authToken();

        //verify sample user is added to database correctly
        Assertions.assertFalse(userService.userDAO.isEmpty(), "New user was not created");
        Assertions.assertFalse(userService.authDAO.isEmpty(), "User was not logged into session properly");
    }

    @Test
    @DisplayName("Normal User Registration")
    void successRegister() throws DataInputException{
        String newUser = "testUser2";
        RegisterRequest registerRequest = new RegisterRequest(newUser, "password", "example@email.com");
        RegisterResult registerResult = userService.register(registerRequest);

        Assertions.assertEquals(newUser, registerResult.username(),
                "Response did not have the same username as was registered");
        Assertions.assertNotNull(registerResult.authToken(), "Response did not contain an authentication string");
        Assertions.assertFalse(registerResult.authToken().isBlank(), "Response contained an empty or blank auth token");
    }

    @Test
    @DisplayName("Register Existing User")
    public void registerTwice() {
        Assertions.assertThrows(DataInputException.class, () -> userService.register(new RegisterRequest(testUser, password, email)));
    }

    @Test
    @DisplayName("Normal User Login")
    void successLogin() throws DataInputException, DataAccessException {
        LoginResult loginResult = userService.login(new LoginRequest(testUser, password));

        Assertions.assertEquals(testUser, loginResult.username(),
                "Response did not have the same username as was registered");
        Assertions.assertNotNull(loginResult.authToken(), "Response did not contain an authentication string");
    }

    @Test
    @DisplayName("Wrong Password Login")
    void loginWrongPassword() {
        Assertions.assertThrows(DataInputException.class, () -> userService.login(new LoginRequest(testUser, "wrongPassword")));
    }

    @Test
    @DisplayName("Wrong Username Login")
    void loginWrongUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest("wrongUser", password)));
    }

    @Test
    @DisplayName("Normal Authentication")
    void successAuthenticate() {
        Assertions.assertEquals(testUser, userService.authenticate(testAuthToken), "authTokens are not equal");
    }

    @Test
    @DisplayName("Bad Token Authentication")
    void authenticateWrongToken() {
        Assertions.assertNull(userService.authenticate("badAuthToken"));
    }

    @Test
    @DisplayName("Normal User Logout")
    void successLogout() throws DataAccessException {
        Assertions.assertEquals(new LogoutResult(), userService.logout(new LogoutRequest(testAuthToken)), "did not logout properly");
        Assertions.assertTrue(userService.authDAO.isEmpty(), "User login session not removed from database");
    }

    @Test
    @DisplayName("Logout User not Logged in")
    void logoutBadAuthToken() throws DataAccessException {
        userService.logout(new LogoutRequest(testAuthToken));
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(new LogoutRequest(testAuthToken)));
    }

    @Test
    void clear() {
        userService.clear();
        Assertions.assertTrue(userService.userDAO.isEmpty() && userService.authDAO.isEmpty(), "database not cleared");
    }

}