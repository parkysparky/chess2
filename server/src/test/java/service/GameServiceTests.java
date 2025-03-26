package service;


import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import model.GameInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DataInputException;
import server.service.GameService;
import server.service.UserService;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGamesRequest;
import server.request.RegisterRequest;
import server.result.ListGamesResult;

import java.util.HashSet;

class GameServiceTests {
    GameService gameService = new GameService();
    UserService userService = new UserService();

    final String gameName = "newGame";
    int gameID;

    final String testUser = "testUser";
    final String password = "password";
    final String email = "example@email.com";

    GameServiceTests() throws DataAccessException {
    }


    @BeforeEach
    void beforeEach() throws DataInputException, DataAccessException {
        gameService.clear();

        gameID = gameService.createGame(new CreateGameRequest(gameName)).gameID();

    }

    @Test
    @DisplayName("List No Games") //later on, fix this text not to have dependency on clear()
    void successListNoGames() throws DataAccessException {
        gameService.clear();

        ListGamesResult listGamesResult = gameService.listGames(new ListGamesRequest());

        Assertions.assertEquals(new ListGamesResult(new HashSet<GameInfo>()), listGamesResult, "Zero games were not listed");
    }

    @Test
    @DisplayName("List One Game")
    void successListOneGame() throws DataAccessException {
        ListGamesResult listGamesResult = gameService.listGames(new ListGamesRequest());

        GameInfo gameInfo = new GameInfo(gameID, null, null, gameName);
        HashSet<GameInfo> gamesList = new HashSet<>();
        gamesList.add(gameInfo);

        ListGamesResult correctResult = new ListGamesResult(gamesList);

        Assertions.assertEquals(correctResult, listGamesResult, "Incorrect game list");
    }

    @Test
    @DisplayName("Normal User Create Game")
    void successCreateGame() {
        Assertions.assertNotNull(gameID, "Result did not return a game ID");
        Assertions.assertTrue(gameID > 0, "Result returned invalid game ID");
    }

    @Test
    @DisplayName("Create Game With Null Game Name")
    void createGameWithoutName() {
        Assertions.assertThrows(DataInputException.class, () -> gameService.createGame(new CreateGameRequest(null)));
    }

    @Test
    @DisplayName("Create Game With Empty Game Name")
    void createGameEmptyName() {
        Assertions.assertThrows(DataInputException.class, () -> gameService.createGame(new CreateGameRequest("")));
    }

    @Test
    @DisplayName("Create Game With Blank Game Name")
    void createGameBlankName() {
        Assertions.assertThrows(DataInputException.class, () -> gameService.createGame(new CreateGameRequest(" ")));
    }

    @Test
    @DisplayName("Normal User Join Game as White") //later rewrite this to remove dependencies as well
    void successJoinGameWhite() throws DataInputException, DataAccessException {
        userService.register(new RegisterRequest(testUser, password, email));

        gameService.joinGame(new JoinGameRequest(testUser, ChessGame.TeamColor.WHITE, gameID));

        GameData joinGameResult = gameService.gameDAO.getGame(gameID);
        GameData correctResult = new GameData(gameID, testUser, null, gameName, new ChessGame());

        Assertions.assertEquals(correctResult, joinGameResult, "User not added to game");
    }

    @Test
    @DisplayName("Normal User Join Game as Black") //later rewrite this to remove dependencies as well
    void successJoinGameBlack() throws DataInputException, DataAccessException {
        userService.register(new RegisterRequest(testUser, password, email));

        gameService.joinGame(new JoinGameRequest(testUser, ChessGame.TeamColor.BLACK, gameID));

        GameData joinGameResult = gameService.gameDAO.getGame(gameID);
        GameData correctResult = new GameData(gameID, null, testUser, gameName, new ChessGame());

        Assertions.assertEquals(correctResult, joinGameResult, "User not added to game");
    }

    @Test
    @DisplayName("Normal User Join 2nd player Black") //later rewrite this to remove dependencies as well
    void successJoinGame2ndBlack() throws DataInputException, DataAccessException {
        //P1 join game
        gameService.joinGame(new JoinGameRequest(testUser, ChessGame.TeamColor.WHITE, gameID));

        //P2 created and join game
        final String testUser2 = "testUser2";
        final String email2 = "example2@mail.com";
        userService.register(new RegisterRequest(testUser2, password, email2));
        gameService.joinGame(new JoinGameRequest(testUser2, ChessGame.TeamColor.BLACK, gameID));


        GameData joinGameResult = gameService.gameDAO.getGame(gameID);
        GameData correctResult = new GameData(gameID, testUser, testUser2, gameName, new ChessGame());

        Assertions.assertEquals(correctResult, joinGameResult, "User not added to game");
    }

    @Test
    @DisplayName("Normal User Join 2nd player White") //later rewrite this to remove dependencies as well
    void successJoinGame2ndWhite() throws DataInputException, DataAccessException {
        //P1 join game
        gameService.joinGame(new JoinGameRequest(testUser, ChessGame.TeamColor.BLACK, gameID));

        //P2 created and join game
        final String testUser2 = "testUser2";
        final String email2 = "example2@mail.com";
        userService.register(new RegisterRequest(testUser2, password, email2));
        gameService.joinGame(new JoinGameRequest(testUser2, ChessGame.TeamColor.WHITE, gameID));


        GameData joinGameResult = gameService.gameDAO.getGame(gameID);
        GameData correctResult = new GameData(gameID, testUser2, testUser, gameName, new ChessGame());

        Assertions.assertEquals(correctResult, joinGameResult, "User not added to game");
    }


    //need to add logic to prevent player from playing self
    //could add logic to prevent multiple registrations with same email
    @Test
    @DisplayName("Join White Taken")
    void joinGameWhiteTaken() throws DataInputException {

        gameService.joinGame(new JoinGameRequest(testUser, ChessGame.TeamColor.WHITE, gameID));

        final String testUser2 = "testUser2";
        final String email2 = "example2@mail.com";
        userService.register(new RegisterRequest(testUser2, password, email2));
        JoinGameRequest requestToError = new JoinGameRequest(testUser2, ChessGame.TeamColor.WHITE, gameID);

        Assertions.assertThrows(DataInputException.class, () -> gameService.joinGame(requestToError));
    }

    @Test
    @DisplayName("Join Black Taken")
    void joinGameBlackTaken() throws DataInputException {

        gameService.joinGame(new JoinGameRequest(testUser, ChessGame.TeamColor.BLACK, gameID));

        final String testUser2 = "testUser2";
        final String email2 = "example2@mail.com";
        userService.register(new RegisterRequest(testUser2, password, email2));
        JoinGameRequest requestToError = new JoinGameRequest(testUser2, ChessGame.TeamColor.BLACK, gameID);

        Assertions.assertThrows(DataInputException.class, () -> gameService.joinGame(requestToError));
    }

    @Test
    void clear() throws DataAccessException {
        gameService.clear();
        Assertions.assertTrue(gameService.gameDAO.isEmpty(), "database not cleared");
    }

}