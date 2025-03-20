package dataaccess;

import chess.ChessGame;
import com.google.gson.GsonBuilder;
import model.GameData;
import model.GameInfo;

import java.util.HashSet;
import java.util.List;

import static dataaccess.DatabaseManager.*;

public class MySQLGameDAO implements GameDAO{

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        //validate input
        if (anyFieldBlank(gameName)) { throw new DataAccessException("bad request"); }

        ChessGame newGame = new ChessGame();
        String serializedGame = new GsonBuilder().create().toJson(newGame);
        var statement = "INSERT INTO gamedata (gameName, game) VALUES (?, ?)";

        return executeUpdate(statement, gameName, serializedGame);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        //validate input
        if (anyFieldBlank(gameID)) { throw new DataAccessException("bad request"); }

        var statement = "SELECT * FROM gamedata WHERE gameID=?";

        List<GameData> gameDataList = executeQuery(statement,
                rs -> (new GameData(rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        new GsonBuilder().create().fromJson(rs.getString("game"), ChessGame.class)))
                ,gameID);

        if(gameDataList.isEmpty()){
            throw new DataAccessException("bad request");
        }

        return gameDataList.getFirst();
    }

    @Override
    public HashSet<GameInfo> listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName  FROM gamedata";

        List<GameInfo> gameDataList = executeQuery(statement,
                rs -> (new GameInfo(rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"))));

        return new HashSet<>(gameDataList);
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        //validate input
        if(anyFieldBlank(gameID, updatedGame)) { throw new DataAccessException("bad request"); }
        if(gameID != updatedGame.gameID()) { throw new DataAccessException("bad request"); }

    }

    @Override
    public void clear() throws DataAccessException {//hard coded table name, use a reference to table instead?
        var statement = "TRUNCATE TABLE gamedata";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    private boolean anyFieldBlank(Object... params) {
        boolean returnValue = false;
        for(var param : params){
            if (param == null){
                returnValue = true;
                break;
            }
            switch(param) {
                case String s -> { if (s == null || s.isBlank()) { returnValue = true; } }
                case Integer i -> { if(i == null) {returnValue = true;} }
                case GameData g -> {returnValue = anyFieldBlank(g.gameID(),
                                                                g.gameName(),
                                                                g.game());}
                case ChessGame cG -> { if(cG == null) {returnValue = true;} }
                default -> returnValue = false;
            }
            if(returnValue) {break;}
        }

        return returnValue;
    }


}
