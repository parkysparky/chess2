package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.DataInputException;
import server.service.GameService;
import server.service.request.CreateGameRequest;

class GameServiceTests {
    GameService gameService = new GameService();

    final String gameName = "newGame";
    int gameID;


    @BeforeEach
    void setUp() throws DataInputException {
        gameService.clear();

        gameID = gameService.createGame(new CreateGameRequest(gameName)).gameID();
    }

    @Test
    void listGames() {
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