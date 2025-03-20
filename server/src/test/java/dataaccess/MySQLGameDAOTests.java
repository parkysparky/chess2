package dataaccess;

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
    void createGameNoName() throws DataAccessException { //depends on clear() and getGame()
        Assertions.assertEquals(gameID, mySQLGameDAO.getGame(gameID).gameID(),
                "generated and found gameIDs do not match");
    }

    @Test
    void getGame() {
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