package server.service;

import model.GameData;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.ListGamesRequest;
import server.service.result.CreateGameResult;
import server.service.result.JoinGameResult;
import server.service.result.ListGamesResult;

import java.util.HashSet;

public class GameService {
    HashSet<GameData> gameData = new HashSet<>();
    public ListGamesResult listGames(ListGamesRequest registerRequest) {

        return null; ////Will need to correct this to pass tests
    }

    public CreateGameResult createGame(CreateGameRequest loginRequest) {
        //plan how this method should work

        return null;
    }

    public JoinGameResult joinGame(JoinGameRequest logoutRequest) {

        return null; ////Will need to correct this to pass tests
    }

    void resetGameData(){
        gameData = new HashSet<>();
    }
}
