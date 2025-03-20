package model;

public record GameInfo(int gameID, String whiteUsername, String blackUsername, String gameName) {
    // Custom constructor using GameData
    public GameInfo(GameData gameData) {
        this(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());
    }
}
