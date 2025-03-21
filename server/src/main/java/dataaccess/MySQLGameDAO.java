package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
import model.GameInfo;

import java.util.ArrayList;
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

        /// PROBLEM: cannot pass NULL vals into executeQuery
        /// TODO: create method here or in DatabaseManager that dynamically creates SQL query
        /// I got help starting one. I added it below. I want to read into it and probably adjust it
        var statement = "UPDATE gamedata SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        executeUpdate(statement, updatedGame.whiteUsername(), updatedGame.blackUsername(), updatedGame.gameName(), updatedGame.game(), gameID);
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        // Validate input
        if (anyFieldBlank(gameID, updatedGame)) {
            throw new DataAccessException("bad request");
        }
        if (gameID != updatedGame.gameID()) {
            throw new DataAccessException("bad request");
        }

        // Prepare the SQL query dynamically
        StringBuilder queryBuilder = new StringBuilder("UPDATE gamedata SET ");
        List<Object> parameters = new ArrayList<>();

        if (updatedGame.whiteUsername() != null) {
            queryBuilder.append("whiteUsername = ?, ");
            parameters.add(updatedGame.whiteUsername());
        }
        if (updatedGame.blackUsername() != null) {
            queryBuilder.append("blackUsername = ?, ");
            parameters.add(updatedGame.blackUsername());
        }
        if (updatedGame.gameName() != null) {
            queryBuilder.append("gameName = ?, ");
            parameters.add(updatedGame.gameName());
        }
        if (updatedGame.game() != null) {
            queryBuilder.append("game = ?, ");
            parameters.add(new Gson().toJson(updatedGame.game())); // Convert game to JSON string
        }

        // If no fields need to be updated, return early
        if (parameters.isEmpty()) {
            throw new DataAccessException("Nothing to update");
        }

        // Remove the last comma and space
        queryBuilder.setLength(queryBuilder.length() - 2);

        // Add the WHERE clause
        queryBuilder.append(" WHERE gameID = ?");
        parameters.add(gameID);

        // Execute the dynamically built update statement
        executeUpdate(queryBuilder.toString(), parameters.toArray());
    }

    @Override
    public void clear() throws DataAccessException {//hard coded table name, use a reference to table instead?
        var statement = "TRUNCATE TABLE gamedata";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public boolean isEmpty() throws DataAccessException {
        var statement = "SELECT COUNT(*) FROM gamedata LIMIT 1;";

        //count number of entries in userData, LIMIT 1 means if any entries stop, not empty
        List<Integer> gameCount = executeQuery(statement,
                rs -> (rs.getInt(1)) );

        //return whether any entries were counted. if count == 0 then isEmpty = true
        return gameCount.getFirst() == 0;
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
