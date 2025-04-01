package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import server.Server;
import server.ServerFacade;
import server.result.ListGamesResult;
import server.result.LoginResult;
import server.result.LogoutResult;
import server.result.RegisterResult;

import java.util.HashSet;
import java.util.stream.Stream;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static final String testUser1 = "testUser1";
    private static final String password = "password";
    private static final String email = "example@email.com";

    private static final String gameName = "testGame";

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);

    }

    @AfterEach
    void afterEach() throws ResponseException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void normalClear() {
        Assertions.assertDoesNotThrow(() -> serverFacade.clear(), "clear() throws an exception");
    }



    @Test
    public void normalRegisterUser() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(testUser1, password, email);

        Assertions.assertNotNull(registerResult, "serverFacade.register returned null");
        Assertions.assertEquals(testUser1, registerResult.username());
    }

    @ParameterizedTest()
    @MethodSource("registerMissingDataTestCases")
    public void registerUserDataMissing(String username, String password, String email) throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(username, email, password));
    }

    private static Stream registerMissingDataTestCases(){
        return Stream.of(
                Arguments.of(null, password, email),
                Arguments.of(testUser1, null, email),
                Arguments.of(testUser1, password, null),
                Arguments.of(" ", password, email),
                Arguments.of(testUser1, " ", email),
                Arguments.of(testUser1, password, " ")
        );
    }



    @Test
    public void normalUserLogin() throws ResponseException {
        serverFacade.register(testUser1, password, email);
        LoginResult loginResult = serverFacade.login(testUser1, password);

        Assertions.assertNotNull(loginResult, "serverFacade.login returned null");
        Assertions.assertNotNull(loginResult.authToken(), "serverFacade.login returned null authToken");
        Assertions.assertEquals(testUser1, loginResult.username(), "serverFacade.login returned unexpected username");
    }

    @Test
    public void loginBadPassword() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(testUser1, password, email);
        Assertions.assertThrows( ResponseException.class, () -> serverFacade.login(testUser1, "badPassword") );
    }

    @Test
    public void loginUserNoExist() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(testUser1, password));
    }

    @ParameterizedTest()
    @MethodSource("loginMissingDataTestCases")
    public void loginUserDataMissing(String username, String password) throws ResponseException {
        RegisterResult registerResult = serverFacade.register(testUser1, password, email);

        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(username, password));
    }

    private static Stream loginMissingDataTestCases(){
        return Stream.of(
                Arguments.of(null, password),
                Arguments.of(testUser1, null),
                Arguments.of(" ", password),
                Arguments.of(testUser1, " ")
        );
    }



    @Test
    public void normalUserLogout() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(testUser1, password, email);
        Assertions.assertEquals(new LogoutResult(), serverFacade.logout(registerResult.authToken()), "User not successfully logged out");
    }

    @ParameterizedTest
    @MethodSource("badAuthInputCases")
    public void logoutBadInput(String authToken) throws ResponseException {
        serverFacade.register(testUser1, password, email);
        Assertions.assertThrows( ResponseException.class, () -> serverFacade.logout(authToken) );
    }

    private static Stream<Arguments> badAuthInputCases() {
        return Stream.of(
                Arguments.of("bad auth token"),
                Arguments.of((String) null), //Junit can't handle a pure null argument, needs to know argument is a null string
                Arguments.of(" ")
        );
    }


    @Test
    public void normalListZeroGames() throws ResponseException {
        String authToken = serverFacade.register(testUser1, password, email).authToken();
        Assertions.assertNull(serverFacade.listGames(authToken));
    }

    @Test
    public void normalListOneGame() throws ResponseException {
        String authToken = serverFacade.register(testUser1, password, email).authToken();
        serverFacade.createGame(authToken, gameName);
        Assertions.assertNotNull(serverFacade.listGames(authToken));
    }

    @Test
    public void normalListMultipleGames() throws ResponseException {

    }


    @Test
    public void normalCreateGame() throws ResponseException {
        String authToken = serverFacade.register(testUser1, password, email).authToken();
        final int[] gameID = new int[1];
        Assertions.assertDoesNotThrow(() -> gameID[0] = serverFacade.createGame(authToken, gameName).gameID());
        Assertions.assertNotNull(gameID[0], "No gameID returned");
    }

    @ParameterizedTest(name = "{index}: {0} authToken input")
    @MethodSource("badAuthInputCases")
    public void createGameBadAuth(String authToken) throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(authToken, gameName));
    }

    @ParameterizedTest(name = "{index}: {0} gameName input")
    @NullAndEmptySource
    public void createGameBadGameName(String authToken) throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(authToken, gameName));
    }

}
