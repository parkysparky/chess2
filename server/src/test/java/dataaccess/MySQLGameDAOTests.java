package dataaccess;

import chess.ChessGame;
import com.google.gson.GsonBuilder;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLGameDAOTests {
    MySQLAuthDAO mySQLAuthDAO;
    MySQLUserDAO mySQLUserDAO;
    MySQLGameDAO mySQLGameDAO;

    int gameID;
    final String gameName = "newGame";

    public MySQLGameDAOTests() throws DataAccessException {
        mySQLAuthDAO = new MySQLAuthDAO();
        mySQLGameDAO = new MySQLGameDAO();
        mySQLUserDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLAuthDAO.clear();
        mySQLGameDAO.clear();
        mySQLUserDAO.clear();

        gameID = mySQLGameDAO.createGame(gameName);
    }

    @Test
    void successCreateGame() throws DataAccessException { //depends on clear() and getGame()
        Assertions.assertEquals(gameID, mySQLGameDAO.getGame(gameID).gameID(),
                "generated and found gameIDs do not match");
    }

    @Test
    void createGameBlankName() { //depends on clear() and getGame()\
        Assertions.assertThrows(DataAccessException.class, () -> { mySQLGameDAO.createGame(" "); });
    }

    @Test
    void createGameNameNull() { //depends on clear() and getGame()
        Assertions.assertThrows(DataAccessException.class, () -> { mySQLGameDAO.createGame(null); });
    }

    @Test
    void successGetGame() throws DataAccessException { //depends on clear() and createGame()
        ChessGame newGame = new ChessGame();
        GameData expectedGame = new GameData(gameID, null, null, gameName, newGame);
        GameData returnedGame = mySQLGameDAO.getGame(gameID);

        Assertions.assertEquals(expectedGame,
                                returnedGame,
                                String.format(  """
                                                Actual gameData did not match expected gameData
                                                Expected gameData:
                                                %s
                                                Actual gameData:
                                                %s
                                                """, expectedGame.toString(), returnedGame.toString()));
    }

    @Test
    void getGameNoGameID() { //depends on clear() and createGame()
        Assertions.assertThrows(DataAccessException.class, () -> { mySQLGameDAO.createGame(null); });
    }

    @Test
    void successListGames() {

    }

    @Test
    void listGames() {

    }

    @Test
    void updateGameInfo() {
    }

    @Test
    void clear() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertTrue(mySQLGameDAO.isEmpty(), "Table is not properly cleared");
    }

    @Test
    void isEmpty() {
    }
}