package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameInfo;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    int gameCount = 0;
    HashSet<GameData> gameData = new HashSet<>();


    private int getGameCount(){
        return gameCount;
    }

    private void incrementGameCount(){
        gameCount++;
    }

    @Override
    public int createGame(String gameName){
        incrementGameCount();
        int gameID = getGameCount();
        gameData.add(new GameData(gameID, null, null, gameName, new ChessGame()));

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for(var game : gameData){
            if(game.gameID() == gameID){
                return game;
            }
        }
        throw new DataAccessException("bad request");
    }

    @Override
    public HashSet<GameInfo> listGames() {
        HashSet<GameInfo> gameInfo = new HashSet<>();
        for(var game : gameData){
            gameInfo.add(new GameInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }

        return gameInfo;
    }

    @Override
    public void updateGameInfo(int gameID, GameInfo newGameInfo) throws DataAccessException {
        //cannot update chess game, only the other data and the old game is passed along
        //to update a chess game use updateGame(int gameID, GameData newGameData)
        GameData oldGame = getGame(gameID);
        gameData.remove(oldGame);
        GameData newGame = new GameData(gameID,
                                        newGameInfo.whiteUsername(),
                                        newGameInfo.blackUsername(),
                                        newGameInfo.gameName(),
                                        oldGame.game());
        gameData.add(newGame);
    }
}
