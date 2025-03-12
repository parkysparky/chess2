package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.GameService;

class GameServiceTests {
    GameService gameService = new GameService();


    @BeforeEach
    void setUp() {
        gameService.clear();
    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }

    @Test
    void joinGame() {
    }

    @Test
    void clear() {
    }
}