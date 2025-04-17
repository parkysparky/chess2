package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;


class MySQLGameDAOTests {
    MySQLUserDAO mySQLUserDAO;
    MySQLGameDAO mySQLGameDAO;

    int gameID;
    final String gameName = "newGame";

    final String testUser = "testUser";
    final String testUser2 = "testUser2";
    final String password = "password";
    final String email = "example@email.com";

    public MySQLGameDAOTests() throws DataAccessException {
        mySQLGameDAO = new MySQLGameDAO();
        mySQLUserDAO = new MySQLUserDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
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
    void successListNoGames() throws DataAccessException {
        mySQLGameDAO.clear();

        String expected = new HashSet<GameData>().toString();
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));
    }

    @Test
    void successListOneGame() throws DataAccessException {
        HashSet<GameData> expectedGameList = new HashSet<>();
        expectedGameList.add(new GameData(gameID, null, null, gameName, new ChessGame()));

        String expected = expectedGameList.toString();
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));
    }

    @Test
    void successListMultipleGames() throws DataAccessException {
        HashSet<GameData> expectedGameList = new HashSet<>();
        expectedGameList.add(new GameData(gameID, null, null, gameName, new ChessGame()));
        final int numGamesToList = 5;

        for(int i = 2; i <= numGamesToList; i++) {
            GameData newGame = mySQLGameDAO.getGame(mySQLGameDAO.createGame(gameName + (i)));
            expectedGameList.add(newGame);
        }

        String expected = expectedGameList.toString();
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));
    }


    @Test
    @DisplayName("White Joins Game Alone")
    void updateGameDataWhiteOnly() throws DataAccessException {
        HashSet<GameData> expectedGameList = new HashSet<>();
        GameData expectedGameData = new GameData(gameID, testUser, null, gameName, new ChessGame());
        expectedGameList.add(expectedGameData);
        String expected = expectedGameList.toString();

        mySQLUserDAO.createUser(testUser, password, email);
        mySQLGameDAO.updateGame(gameID, expectedGameData);
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));
    }

    @Test
    @DisplayName("Black Joins Game Alone")
    void updateGameDataBlackOnly() throws DataAccessException {
        HashSet<GameData> expectedGameList = new HashSet<>();
        GameData expectedGameData = new GameData(gameID, null, testUser, gameName, new ChessGame());
        expectedGameList.add(expectedGameData);
        String expected = expectedGameList.toString();

        mySQLUserDAO.createUser(testUser, password, email);
        mySQLGameDAO.updateGame(gameID, expectedGameData);
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));
    }

    @Test
    @DisplayName("White then Black Join Game")
    void updateGameDataWhiteFirst() throws DataAccessException {
        HashSet<GameData> expectedGameList = new HashSet<>();
        GameData expectedGameData = new GameData(gameID, testUser, testUser2, gameName, new ChessGame());
        expectedGameList.add(expectedGameData);
        String expected = expectedGameList.toString();

        mySQLUserDAO.createUser(testUser, password, email);
        mySQLUserDAO.createUser(testUser2, password, email);
        mySQLGameDAO.updateGame(gameID, new GameData(gameID, testUser, null, gameName, new ChessGame()));
        mySQLGameDAO.updateGame(gameID, expectedGameData);
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));


    }

    @Test
    @DisplayName("Black then White Join Game")
    void updateGameDataBlackFirst() throws DataAccessException {
        HashSet<GameData> expectedGameList = new HashSet<>();
        GameData expectedGameData = new GameData(gameID, testUser2, testUser, gameName, new ChessGame());
        expectedGameList.add(expectedGameData);
        String expected = expectedGameList.toString();

        mySQLUserDAO.createUser(testUser, password, email);
        mySQLUserDAO.createUser(testUser2, password, email);
        mySQLGameDAO.updateGame(gameID, new GameData(gameID, null, testUser, gameName, new ChessGame()));
        mySQLGameDAO.updateGame(gameID, expectedGameData);
        String actual = mySQLGameDAO.listGames().toString();
        Assertions.assertEquals(expected, actual, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expected, actual));
    }


    @Test
    void successViewNewGame() throws DataAccessException{
        ChessGame expectedGame = new ChessGame();

        ChessGame actualGame = mySQLGameDAO.getGame(gameID).game();
        Assertions.assertEquals(expectedGame, actualGame, String.format("""
                                                Actual game list did not match expected game list
                                                Expected game list:
                                                %s
                                                Actual game list:
                                                %s
                                                """, expectedGame, actualGame));
    }

    @Test
    void viewGameBadID(){
        int badGameID = -1;
        Assertions.assertThrows(DataAccessException.class, () -> mySQLGameDAO.getGame(badGameID).game());
    }


    @Test
    void clear() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertTrue(mySQLGameDAO.isEmpty(), "Table is not properly cleared");
    }

    @Test
    void successIsEmpty() throws DataAccessException {
        mySQLGameDAO.clear();
        Assertions.assertTrue(mySQLGameDAO.isEmpty(), "Table is not properly cleared");
    }

    @Test
    void successIsNotEmpty() throws DataAccessException {
        Assertions.assertFalse(mySQLGameDAO.isEmpty(), "Table is not properly populated");
    }
}