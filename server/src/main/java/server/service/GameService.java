package server.service;

import model.GameData;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.ListGamesRequest;

import java.util.HashSet;

public class GameService {//TODO create these
    HashSet<GameData> gameData = new HashSet<>();
//    public ListGamesResult register(ListGamesRequest registerRequest) {}
//
//    public CreateGameResult login(CreateGameRequest loginRequest) {}
//
//    public JoinGameResult logout(JoinGameRequest logoutRequest) {}
    void resetGameData(){
        gameData = new HashSet<>();
    }
}
