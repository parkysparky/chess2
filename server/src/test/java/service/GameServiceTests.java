package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.service.GameService;
import server.service.UserService;

class GameServiceTests {
    UserService userService = new UserService();
    GameService gameService = new GameService();


    @BeforeEach
    void setUp() {
        userService.clear();
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