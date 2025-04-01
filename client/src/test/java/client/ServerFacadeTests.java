package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import server.result.RegisterResult;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private final String testUser1 = "testUser1";
    private final String password = "password";
    private final String email = "example@email.com";

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void normalRegisterUser() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(testUser1, password, email);

        Assertions.assertNotNull(registerResult, "serverFacade.register returned null");
        Assertions.assertEquals(testUser1, registerResult.username());
    }

}
