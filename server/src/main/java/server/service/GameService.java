package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import server.DataInputException;
import server.service.request.CreateGameRequest;
import server.service.request.JoinGameRequest;
import server.service.request.ListGamesRequest;
import server.service.result.CreateGameResult;
import server.service.result.JoinGameResult;
import server.service.result.ListGamesResult;

import static chess.ChessGame.TeamColor.BLACK;

public class GameService {
    //this is public so that the unit tests can call to it. find a better solution
    public GameDAO gameDAO;

    public GameService() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
    }

    public GameService(boolean useMySQL) throws DataAccessException {
        if(useMySQL){
            gameDAO = new MySQLGameDAO();
        }
        else{
            gameDAO = new MemoryGameDAO();
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataInputException, DataAccessException {
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
                    GameData updatedGameData = new GameData(gameID, gameToJoin.whiteUsername(), joinGameRequest.username(), gameToJoin.gameName(), gameToJoin.game());
                    gameDAO.updateGame(gameID, updatedGameData);
                }
                else {
                    throw new DataInputException("already taken");
                }
            }
            else{
                if(gameToJoin.whiteUsername() == null){  //check spot is open, add user if it is
                    GameData updatedGameData = new GameData(gameID, joinGameRequest.username(), gameToJoin.blackUsername(), gameToJoin.gameName(), gameToJoin.game());
                    gameDAO.updateGame(gameID, updatedGameData);
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

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
