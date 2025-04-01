package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import server.Server;
import server.ServerFacade;
import server.result.LoginResult;
import server.result.RegisterResult;

import java.util.stream.Stream;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static final String testUser1 = "testUser1";
    private static final String password = "password";
    private static final String email = "example@email.com";

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

    @ParameterizedTest()
    @MethodSource("loginMissingDataTestCases")
    public void loginUserDataMissing(String username, String password) throws ResponseException {
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

}
