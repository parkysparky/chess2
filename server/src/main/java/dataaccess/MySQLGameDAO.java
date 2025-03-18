package dataaccess;

import model.GameData;
import model.GameInfo;

import java.util.HashSet;

import static dataaccess.DatabaseManager.configureDatabase;

public class MySQLGameDAO implements GameDAO{

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) {
        //TODO: create a Json serializer for ChessGame so that I can pass that into the executeUpdate() function
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public HashSet<GameInfo> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGameInfo(int gameID, GameInfo newGame) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
