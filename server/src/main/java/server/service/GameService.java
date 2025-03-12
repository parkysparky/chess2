package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import model.GameInfo;
import server.DataInputException;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.ListGamesRequest;
import server.service.result.CreateGameResult;
import server.service.result.JoinGameResult;
import server.service.result.ListGamesResult;

import static chess.ChessGame.TeamColor.BLACK;

public class GameService {
    MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

    public ListGamesResult listGames(ListGamesRequest ListGamesRequest) {
        return new ListGamesResult(memoryGameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        int gameID = memoryGameDAO.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataInputException {
        ChessGame.TeamColor playerColor = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();
        try{
            GameData gameToJoin = memoryGameDAO.getGame(gameID);
            if(playerColor == BLACK){
                if(gameToJoin.blackUsername() == null){  //check spot is open, add user if it is
                    GameInfo newGameInfo = new GameInfo(gameID, gameToJoin.whiteUsername(), joinGameRequest.username(), gameToJoin.gameName());
                    memoryGameDAO.updateGameInfo(gameID, newGameInfo);
                }
                else {
                    throw new DataInputException("already taken");
                }
            }
            else{
                if(gameToJoin.whiteUsername() == null){  //check spot is open, add user if it is
                    GameInfo newGameInfo = new GameInfo(gameID, joinGameRequest.username(), gameToJoin.blackUsername(), gameToJoin.gameName());
                    memoryGameDAO.updateGameInfo(gameID, newGameInfo);
                }
                else {
                    throw new DataInputException("already taken");
                }
            }
        }
        catch (DataAccessException e){
            throw new DataInputException("bad request");
        }

        return null;
    }

    public void clear(){
        memoryGameDAO.clear();
    }
}
