package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    int gameCount = 1;
    HashSet<GameData> gameData = new HashSet<>();


    private int getGameCount(){
        return gameCount;
    }

    private void incrementGameCount(){
        gameCount++;
    }

    @Override
    public int createGame(String gameName){
        int gameID = getGameCount();
        gameData.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        incrementGameCount();

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame() {

    }
}
