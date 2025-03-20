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
            gameInfo.add(new GameInfo(game));
        }

        return gameInfo;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        //validate input
        if(anyFieldBlank(gameID, updatedGame)) { throw new DataAccessException("bad request"); }
        if(gameID != updatedGame.gameID()) { throw new DataAccessException("bad request"); }

        GameData oldGame = getGame(gameID);
        gameData.remove(oldGame);
        GameData newGame = new GameData(gameID,
                updatedGame.whiteUsername(),
                updatedGame.blackUsername(),
                updatedGame.gameName(),
                updatedGame.game());
        gameData.add(newGame);
    }

    @Override
    public void clear() {
        gameCount = 0;
        gameData = new HashSet<>();
    }

    @Override
    public boolean isEmpty() {
        return gameData.isEmpty();
    }

    private boolean anyFieldBlank(Object... params) {
        boolean returnValue = false;
        for(var param : params){
            if (param == null){
                returnValue = true;
                break;
            }
            switch(param) {
                case String s -> { if (s.isBlank()) { returnValue = true; } }
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
