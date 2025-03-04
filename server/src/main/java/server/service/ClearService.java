package server.service;

import server.service.result.ClearResult;

public class ClearService {
    private void clearUserData(UserService userService){
        userService.clearUserData();
    }
    private void clearAuthData(UserService userService){
        userService.clearAuthData();
    }
    private void clearGameData(GameService gameService){
        gameService.resetGameData();
    }
    public ClearResult clearAllData(UserService userService, GameService gameService){
        clearUserData(userService);
        clearAuthData(userService);
        clearGameData(gameService);

        return new ClearResult();
    }
}
