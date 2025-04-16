package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameInfo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import server.DataInputException;
import server.Server;
import server.facade.ServerFacade;
import server.request.ViewGameRequest;
import server.result.*;

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
        RegisterResult registerResult = serverFacade.register(testUser1, this.password, email);

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
    public void normalViewNewGame(){
        ChessGame expectedGame = new ChessGame();

        final ChessGame[] returnedGame = new ChessGame[1];
        Assertions.assertDoesNotThrow(() -> {
            final String authToken = serverFacade.register(testUser1, password, email).authToken();
            final int gameID = serverFacade.createGame(authToken, gameName).gameID();

            returnedGame[0] = serverFacade.viewGame(authToken, gameID).game();

        });

        ChessGame actualGame = returnedGame[0];

        Assertions.assertEquals(expectedGame, actualGame, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expectedGame, actualGame));
    }

    @Test
    void viewGameBadID(){
        int badGameID = -1;
        final String authToken[] = new String[1];
        final int gameID[] = new int[1];
        Assertions.assertDoesNotThrow(() -> {
            authToken[0] = serverFacade.register(testUser1, password, email).authToken();
            gameID[0] = serverFacade.createGame(authToken[0], gameName).gameID();
        });

        Assertions.assertThrows(ResponseException.class, () -> serverFacade.viewGame(authToken[0], gameID[0]).game());
    }



    @Test
    public void normalListZeroGames() throws ResponseException {
        String authToken = serverFacade.register(testUser1, password, email).authToken();

        ListGamesResult actualResult = serverFacade.listGames(authToken);
        ListGamesResult expectedResult = new ListGamesResult(new HashSet<GameInfo>());

        Assertions.assertEquals(expectedResult, actualResult, "Zero games were not listed");
    }

    @Test
    public void normalListOneGame() throws ResponseException {
        String authToken = serverFacade.register(testUser1, password, email).authToken();

        int gameID = serverFacade.createGame(authToken, gameName).gameID();
        GameInfo createdGame = new GameInfo(gameID, null, null, gameName);
        HashSet<GameInfo> gameList = new HashSet<>();
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
        String authToken = serverFacade.register(testUser1, password, email).authToken();

        int gameID = serverFacade.createGame(authToken, gameName).gameID();
        String newGameName = gameName;
        GameInfo newGame = new GameInfo(gameID, null, null, gameName);
        HashSet<GameInfo> expectedGameList = new HashSet<>();
        expectedGameList.add(newGame);

        final int numGamesToList = 5;

        for(int i = 2; i <= numGamesToList; i++) {
            newGameName = gameName + i;
            gameID = serverFacade.createGame(authToken, newGameName).gameID();
            newGame = new GameInfo(gameID, null,null , gameName + i);
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
        String authToken = serverFacade.register(testUser1, password, email).authToken();
        final int[] gameID = new int[1];
        Assertions.assertDoesNotThrow(() -> gameID[0] = serverFacade.createGame(authToken, gameName).gameID());
        Assertions.assertNotNull(gameID[0], "No gameID returned");
    }

    @ParameterizedTest(name = "{index}: {0} authToken input")
    @MethodSource("badAuthInputCases")
    public void createGameBadAuth(String authToken) {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(authToken, gameName));
    }

    @ParameterizedTest(name = "{index}: {0} gameName input")
    @NullAndEmptySource
    public void createGameBadGameName(String authToken) {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(authToken, gameName));
    }



    @ParameterizedTest(name = "{index}: 1 Player joins game as {0}")
    @EnumSource(ChessGame.TeamColor.class)
    public void normal1PlayerJoinGame(ChessGame.TeamColor playerColor) {
        final String[] authToken = new String[1];
        final int[] newGameID = new int[1];
        final GameInfo[] actualResult = new GameInfo[1];
        Assertions.assertDoesNotThrow(() -> {
            authToken[0] = serverFacade.register(testUser1, password, email).authToken();
            newGameID[0] = serverFacade.createGame(authToken[0], gameName).gameID();

            serverFacade.joinGame(authToken[0], testUser1, playerColor, newGameID[0]);
            actualResult[0] = serverFacade.listGames(authToken[0]).games().iterator().next();
        } );

        GameInfo expectedResult;
        if(playerColor == ChessGame.TeamColor.WHITE){
            expectedResult = new GameInfo(newGameID[0], testUser1, null, gameName);
        }
        else{
            expectedResult = new GameInfo(newGameID[0], null, testUser1, gameName);
        }

        Assertions.assertEquals(expectedResult, actualResult[0], "User not added to game");
    }

    @ParameterizedTest(name = "{index}: both players join game as {0}")
    @MethodSource("playerColorsGoodInput")
    public void successTwoPlayersJoin(ChessGame.TeamColor player1Color, ChessGame.TeamColor player2Color){
        final String[] authToken = new String[2];
        final String testUser2 = "testUser2";
        final int[] gameID = new int[1];
        final GameInfo[] actualResult = new GameInfo[1];
        Assertions.assertDoesNotThrow(() -> {
            authToken[0] = serverFacade.register(testUser1, password, email).authToken();
            gameID[0] = serverFacade.createGame(authToken[0], gameName).gameID();
            serverFacade.joinGame(authToken[0], testUser1, player1Color, gameID[0]);

            authToken[1] = serverFacade.register(testUser2, password, email).authToken();

            serverFacade.joinGame(authToken[1], testUser2, player2Color, gameID[0]);
            actualResult[0] = serverFacade.listGames(authToken[0]).games().iterator().next();
        } );

        GameInfo expectedResult;
        if(player1Color == ChessGame.TeamColor.WHITE){
            expectedResult = new GameInfo(gameID[0], testUser1, testUser2, gameName);
        }
        else{
            expectedResult = new GameInfo(gameID[0], testUser2, testUser1, gameName);
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
            authToken[0] = serverFacade.register(testUser1, password, email).authToken();
            gameID[0] = serverFacade.createGame(authToken[0], gameName).gameID();
            serverFacade.joinGame(authToken[0], testUser1, playerColor, gameID[0]);

            authToken[1] = serverFacade.register(testUser2, password, email).authToken();
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
