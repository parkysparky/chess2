package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.service.GameService;
import server.service.UserService;

class UserServiceTests {
    UserService userService = new UserService();
    GameService gameService = new GameService();


    @BeforeEach
    void setUp() {
        userService.clear();
        gameService.clear();
    }

    @Test
    @DisplayName("Normal User Registration")
    void successRegister() {
    }

    @Test
    @DisplayName("Normal User Login")
    void successLogin() {
    }

    @Test
    @DisplayName("Normal User Authentication")
    void successAuthenticate() {
    }

    @Test
    @DisplayName("Normal User Logout")
    void successLogout() {
    }

    @Test
    void clear() {
    }

}