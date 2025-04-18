package server.service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MySQLGameDAO;
import model.GameData;
import server.DataInputException;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGamesRequest;
import server.result.CreateGameResult;
import server.result.JoinGameResult;
import server.result.ListGamesResult;

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
                //check desired spot is open, add user if true
                if(gameToJoin.blackUsername() == null){
                    gameDAO.updateGame(gameID, updateGamePlayer(gameToJoin.whiteUsername(), joinGameRequest.username(), gameToJoin));
                }
                else {
                    throw new DataInputException("already taken");
                }
            }
            else{
                //check desired spot is open, add user to game if true
                if(gameToJoin.whiteUsername() == null){
                    gameDAO.updateGame(gameID, updateGamePlayer(joinGameRequest.username(), gameToJoin.blackUsername(), gameToJoin));
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

    private GameData updateGamePlayer(String whiteUsername, String blackUsername, GameData oldGame){
        return new GameData(oldGame.gameID(), whiteUsername, blackUsername, oldGame.gameName(), oldGame.game());
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
