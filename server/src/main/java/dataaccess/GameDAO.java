package dataaccess;

import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    HashSet<GameData> listGames() throws DataAccessException;
    void updateGame(); /// this will change when I know how I want it to work
    /* Updates a chess game. It should replace the chess game string corresponding to a given gameID.
     * This is used when players join a game or when a move is made.
     */
}
