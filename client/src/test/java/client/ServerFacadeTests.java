package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import server.Server;
import server.facade.ServerFacade;
import server.result.*;

import java.util.HashSet;
import java.util.stream.Stream;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static final String TEST_USER_1 = "testUser1";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "example@email.com";

    private static final String GAME_NAME = "testGame";



    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    void clearAfterEach() throws ResponseException {
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
    public void clearEmptyDatabase() throws ResponseException {
        serverFacade.clear();
        Assertions.assertDoesNotThrow(() -> serverFacade.clear(), "clear() throws an exception");
    }



    @Test
    public void normalRegisterUser() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL);

        Assertions.assertNotNull(registerResult, "serverFacade.register returned null");
        Assertions.assertEquals(TEST_USER_1, registerResult.username());
    }

    @ParameterizedTest()
    @MethodSource("registerMissingDataTestCases")
    public void registerUserDataMissing(String username, String password, String email) throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(username, email, password));
    }

    private static Stream registerMissingDataTestCases(){
        return Stream.of(
                Arguments.of(null, PASSWORD, EMAIL),
                Arguments.of(TEST_USER_1, null, EMAIL),
                Arguments.of(TEST_USER_1, PASSWORD, null),
                Arguments.of(" ", PASSWORD, EMAIL),
                Arguments.of(TEST_USER_1, " ", EMAIL),
                Arguments.of(TEST_USER_1, PASSWORD, " ")
        );
    }



    @Test
    public void normalUserLogin() throws ResponseException {
        serverFacade.register(TEST_USER_1, PASSWORD, EMAIL);
        LoginResult loginResult = serverFacade.login(TEST_USER_1, PASSWORD);

        Assertions.assertNotNull(loginResult, "serverFacade.login returned null");
        Assertions.assertNotNull(loginResult.authToken(), "serverFacade.login returned null authToken");
        Assertions.assertEquals(TEST_USER_1, loginResult.username(), "serverFacade.login returned unexpected username");
    }

    @Test
    public void loginBadPassword() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL);
        Assertions.assertThrows( ResponseException.class, () -> serverFacade.login(TEST_USER_1, "badPassword") );
    }

    @Test
    public void loginUserNoExist() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(TEST_USER_1, PASSWORD));
    }

    @ParameterizedTest()
    @MethodSource("loginMissingDataTestCases")
    public void loginUserDataMissing(String username, String password) throws ResponseException {
        RegisterResult registerResult = serverFacade.register(TEST_USER_1, this.PASSWORD, EMAIL);

        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(username, password));
    }

    private static Stream loginMissingDataTestCases(){
        return Stream.of(
                Arguments.of(null, PASSWORD),
                Arguments.of(TEST_USER_1, null),
                Arguments.of(" ", PASSWORD),
                Arguments.of(TEST_USER_1, " ")
        );
    }



    @Test
    public void normalUserLogout() throws ResponseException {
        RegisterResult registerResult = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL);
        Assertions.assertEquals(new LogoutResult(), serverFacade.logout(registerResult.authToken()), "User not successfully logged out");
    }

    @ParameterizedTest
    @MethodSource("badAuthInputCases")
    public void logoutBadInput(String authToken) throws ResponseException {
        serverFacade.register(TEST_USER_1, PASSWORD, EMAIL);
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
        String authToken = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();

        ListGamesResult actualResult = serverFacade.listGames(authToken);
        ListGamesResult expectedResult = new ListGamesResult(new HashSet<GameData>());

        Assertions.assertEquals(expectedResult, actualResult, "Zero games were not listed");
    }

    @Test
    public void normalListOneGame() throws ResponseException {
        String authToken = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();

        int gameID = serverFacade.createGame(authToken, GAME_NAME).gameID();
        GameData createdGame = new GameData(gameID, null, null, GAME_NAME, new ChessGame());
        HashSet<GameData> gameList = new HashSet<>();
        gameList.add(createdGame);

        ListGamesResult actualResult = serverFacade.listGames(authToken);
        ListGamesResult expectedResult = new ListGamesResult(gameList);

        Assertions.assertEquals(expectedResult, actualResult, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expectedResult, actualResult));
    }

    @Test
    public void normalListMultipleGames() throws ResponseException {
        String authToken = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();

        int gameID = serverFacade.createGame(authToken, GAME_NAME).gameID();
        String newGameName = GAME_NAME;
        GameData newGame = new GameData(gameID, null, null, GAME_NAME, new ChessGame());
        HashSet<GameData> expectedGameList = new HashSet<>();
        expectedGameList.add(newGame);

        final int numGamesToList = 5;

        for(int i = 2; i <= numGamesToList; i++) {
            newGameName = GAME_NAME + i;
            gameID = serverFacade.createGame(authToken, newGameName).gameID();
            newGame = new GameData(gameID, null,null , GAME_NAME + i, new ChessGame());
            expectedGameList.add(newGame);
        }

        ListGamesResult actualResult = serverFacade.listGames(authToken);
        ListGamesResult expectedResult = new ListGamesResult(expectedGameList);

        Assertions.assertEquals(expectedResult, actualResult, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expectedResult, actualResult));
    }

    @ParameterizedTest(name = "{index}: {0} authToken input")
    @MethodSource("badAuthInputCases")
    public void listGamesBadAuth(String authToken) {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(authToken));
    }



    @Test
    public void normalCreateGame() throws ResponseException {
        String authToken = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();
        final int[] gameID = new int[1];
        Assertions.assertDoesNotThrow(() -> gameID[0] = serverFacade.createGame(authToken, GAME_NAME).gameID());
        Assertions.assertNotNull(gameID[0], "No gameID returned");
    }

    @ParameterizedTest(name = "{index}: {0} authToken input")
    @MethodSource("badAuthInputCases")
    public void createGameBadAuth(String authToken) {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(authToken, GAME_NAME));
    }

    @ParameterizedTest(name = "{index}: {0} gameName input")
    @NullAndEmptySource
    public void createGameBadGameName(String authToken) {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(authToken, GAME_NAME));
    }



    @ParameterizedTest(name = "{index}: 1 Player joins game as {0}")
    @EnumSource(ChessGame.TeamColor.class)
    public void normal1PlayerJoinGame(ChessGame.TeamColor playerColor) {
        final String[] authToken = new String[1];
        final int[] newGameID = new int[1];
        final GameData[] actualResult = new GameData[1];
        Assertions.assertDoesNotThrow(() -> {
            authToken[0] = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();
            newGameID[0] = serverFacade.createGame(authToken[0], GAME_NAME).gameID();

            serverFacade.joinGame(authToken[0], TEST_USER_1, playerColor, newGameID[0]);
            actualResult[0] = serverFacade.listGames(authToken[0]).games().iterator().next();
        } );

        GameData expectedResult;
        if(playerColor == ChessGame.TeamColor.WHITE){
            expectedResult = new GameData(newGameID[0], TEST_USER_1, null, GAME_NAME, new ChessGame());
        }
        else{
            expectedResult = new GameData(newGameID[0], null, TEST_USER_1, GAME_NAME, new ChessGame());
        }

        Assertions.assertEquals(expectedResult, actualResult[0], "User not added to game");
    }

    @ParameterizedTest(name = "{index}: both players join game as {0}")
    @MethodSource("playerColorsGoodInput")
    public void successTwoPlayersJoin(ChessGame.TeamColor player1Color, ChessGame.TeamColor player2Color){
        final String[] authToken = new String[2];
        final String testUser2 = "testUser2";
        final int[] gameID = new int[1];
        final GameData[] actualResult = new GameData[1];
        Assertions.assertDoesNotThrow(() -> {
            authToken[0] = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();
            gameID[0] = serverFacade.createGame(authToken[0], GAME_NAME).gameID();
            serverFacade.joinGame(authToken[0], TEST_USER_1, player1Color, gameID[0]);

            authToken[1] = serverFacade.register(testUser2, PASSWORD, EMAIL).authToken();

            serverFacade.joinGame(authToken[1], testUser2, player2Color, gameID[0]);
            actualResult[0] = serverFacade.listGames(authToken[0]).games().iterator().next();
        } );

        GameData expectedResult;
        if(player1Color == ChessGame.TeamColor.WHITE){
            expectedResult = new GameData(gameID[0], TEST_USER_1, testUser2, GAME_NAME, new ChessGame());
        }
        else{
            expectedResult = new GameData(gameID[0], testUser2, TEST_USER_1, GAME_NAME, new ChessGame());
        }

        Assertions.assertEquals(expectedResult, actualResult[0], "User not added to game");
    }

    @ParameterizedTest(name = "{index}: both players join game as {0}")
    @EnumSource(ChessGame.TeamColor.class)
    public void twoPlayersJoinAsSameColor(ChessGame.TeamColor playerColor){
        final String[] authToken = new String[2];
        final String testUser2 = "testUser2";
        final int[] gameID = new int[1];
        Assertions.assertDoesNotThrow(() -> {
            authToken[0] = serverFacade.register(TEST_USER_1, PASSWORD, EMAIL).authToken();
            gameID[0] = serverFacade.createGame(authToken[0], GAME_NAME).gameID();
            serverFacade.joinGame(authToken[0], TEST_USER_1, playerColor, gameID[0]);

            authToken[1] = serverFacade.register(testUser2, PASSWORD, EMAIL).authToken();
        } );

        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(authToken[1], testUser2, playerColor, gameID[0]);
        });
    }

    private static Stream<Arguments> playerColorsGoodInput() {
        return Stream.of(
                Arguments.of(ChessGame.TeamColor.BLACK, ChessGame.TeamColor.WHITE),
                Arguments.of(ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK)
        );
    }
}
