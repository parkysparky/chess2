package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
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
    public GameDAO gameDAO = new MemoryGameDAO();

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataInputException {
        if(createGameRequest.gameName() == null || createGameRequest.gameName().isBlank()){
            throw new DataInputException("Game must have a name");
        }
        int gameID = gameDAO.createGame(createGameRequest.gameName());

        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataInputException {
        ChessGame.TeamColor playerColor = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();
        try{
            GameData gameToJoin = gameDAO.getGame(gameID);
            if(playerColor == BLACK){
                if(gameToJoin.blackUsername() == null){  //check spot is open, add user if it is
                    GameInfo newGameInfo = new GameInfo(gameID, gameToJoin.whiteUsername(), joinGameRequest.username(), gameToJoin.gameName());
                    gameDAO.updateGameInfo(gameID, newGameInfo);
                }
                else {
                    throw new DataInputException("already taken");
                }
            }
            else{
                if(gameToJoin.whiteUsername() == null){  //check spot is open, add user if it is
                    GameInfo newGameInfo = new GameInfo(gameID, joinGameRequest.username(), gameToJoin.blackUsername(), gameToJoin.gameName());
                    gameDAO.updateGameInfo(gameID, newGameInfo);
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
        gameDAO.clear();
    }
}
