package service;


import dataaccess.DataAccessException;
import model.GameInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DataInputException;
import server.service.GameService;
import server.service.request.CreateGameRequest;
import server.service.request.ListGamesRequest;
import server.service.result.ListGamesResult;

import java.util.HashSet;

class GameServiceTests {
    GameService gameService = new GameService();

    final String gameName = "newGame";
    int gameID;



    @BeforeEach
    void beforeEach() throws DataInputException {
        gameService.clear();

        gameID = gameService.createGame(new CreateGameRequest(gameName)).gameID();
    }

    @Test
    @DisplayName("List No Games") //later on, fix this text not to have dependency on clear()
    void successListNoGames() throws DataAccessException {
        gameService.clear();

        ListGamesResult listGamesResult = gameService.listGames(new ListGamesRequest());

        Assertions.assertEquals(listGamesResult, new ListGamesResult(new HashSet<GameInfo>()), "Zero games were not listed");
    }

    @Test
    @DisplayName("List One Game")
    void successListOneGame() throws DataAccessException {
        ListGamesResult listGamesResult = gameService.listGames(new ListGamesRequest());

        GameInfo gameInfo = new GameInfo(gameID, null, null, gameName);
        HashSet<GameInfo> gamesList = new HashSet<>();
        gamesList.add(gameInfo);

        ListGamesResult correctResult = new ListGamesResult(gamesList);

        Assertions.assertEquals(listGamesResult, correctResult, "Incorrect game list");
    }

    @Test
    @DisplayName("Normal User Create Game")
    void successCreateGame() {
        Assertions.assertNotNull(gameID, "Result did not return a game ID");
        Assertions.assertTrue(gameID > 0, "Result returned invalid game ID");
    }

    @Test
    @DisplayName("Create Game With Null Game Name")
    void CreateGameWithoutName() {
        Assertions.assertThrows(DataInputException.class, () -> gameService.createGame(new CreateGameRequest(null)));
    }

    @Test
    @DisplayName("Create Game With Empty Game Name")
    void CreateGameEmptyName() {
        Assertions.assertThrows(DataInputException.class, () -> gameService.createGame(new CreateGameRequest("")));
    }

    @Test
    @DisplayName("Create Game With Blank Game Name")
    void CreateGameBlankName() {
        Assertions.assertThrows(DataInputException.class, () -> gameService.createGame(new CreateGameRequest(" ")));
    }

    @Test
    void joinGame() {
    }

    @Test
    void clear() {
    }
}