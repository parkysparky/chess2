package dataaccess;

import model.GameData;
import model.GameInfo;

import java.util.HashSet;

public interface GameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID) throws DataAccessException;
    HashSet<GameInfo> listGames() throws DataAccessException;
    void updateGameInfo(int gameID, GameInfo newGame) throws DataAccessException;
    void clear();
    boolean isEmpty();
}
