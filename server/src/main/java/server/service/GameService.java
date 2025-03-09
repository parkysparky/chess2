package server.service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import model.GameData;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.ListGamesRequest;
import server.service.result.CreateGameResult;
import server.service.result.JoinGameResult;
import server.service.result.ListGamesResult;

import java.util.HashSet;

public class GameService {
    MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    public ListGamesResult listGames(ListGamesRequest ListGamesRequest) {

        return null; ////Will need to correct this to pass tests
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        int gameID = memoryGameDAO.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest logoutRequest) {

        return null; ////Will need to correct this to pass tests
    }

    void resetGameData(){
        memoryGameDAO = new MemoryGameDAO();
    }
}
