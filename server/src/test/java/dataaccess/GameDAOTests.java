package dataaccess;

import chess.*;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {
    GameData newGame = new GameData(1, null, null, "TestGame", new ChessGame());


    static GameDAO GAME_DAO = new SQLGameDAO();

    @BeforeAll
    public static void clear() throws DataAccessException {
        GAME_DAO.clear();
    }

    @Test
    @Order(1)
    public void createGamePositive() throws DataAccessException {
        int gameID = GAME_DAO.createGame(newGame);

        GameData dbGame = GAME_DAO.getGame(gameID);

        Assertions.assertEquals(gameID, dbGame.gameID());
        Assertions.assertEquals(newGame.whiteUsername(), dbGame.whiteUsername());
        Assertions.assertEquals(newGame.blackUsername(), dbGame.blackUsername());
        Assertions.assertEquals(newGame.gameName(), dbGame.gameName());
        Assertions.assertEquals(newGame.game(), dbGame.game());
    }

    @Test
    @Order(2)
    public void createGameNegative(){
        Assertions.assertThrows(DataAccessException.class,
                () -> GAME_DAO.createGame(new GameData(0,null, null, null, new ChessGame())));
        Assertions.assertThrows(DataAccessException.class,
                () -> GAME_DAO.createGame(new GameData(0,null, null, "test", null)));
    }

    @Test
    @Order(3)
    public void getGamePositive() throws DataAccessException {
        GameData dbGame = GAME_DAO.getGame(1);

        Assertions.assertEquals(1, dbGame.gameID());
        Assertions.assertEquals(newGame.whiteUsername(), dbGame.whiteUsername());
        Assertions.assertEquals(newGame.blackUsername(), dbGame.blackUsername());
        Assertions.assertEquals(newGame.gameName(), dbGame.gameName());
        Assertions.assertEquals(newGame.game(), dbGame.game());
    }

    @Test
    @Order(4)
    public void getGameNegative() throws DataAccessException {
        Assertions.assertNull(GAME_DAO.getGame(100));
    }

    @Test
    @Order(5)
    public void listGamePositive() throws DataAccessException {
        GameData bonusGame = new GameData(2, "MyMom", "MyDad", "TheBestGame", new ChessGame());
        GAME_DAO.createGame(bonusGame);

        Collection<GameData> games = GAME_DAO.listGames();

        Assertions.assertTrue(games.containsAll(List.of(newGame, bonusGame)));

    }

    @Test
    @Order(6)
    public void updateGamePositive() throws DataAccessException, InvalidMoveException {
        newGame.game().getBoard().resetBoard();

        GAME_DAO.updateGame(newGame);
        GameData dbGame = GAME_DAO.getGame(1);

        Assertions.assertEquals(newGame, dbGame);
        Assertions.assertEquals(newGame.game().getBoard(), dbGame.game().getBoard());

        newGame.game().makeMove(new ChessMove(new ChessPosition(2,4), new ChessPosition(4, 4), null));

        GAME_DAO.updateGame(newGame);
        dbGame = GAME_DAO.getGame(1);

        Assertions.assertEquals(newGame, dbGame);
        Assertions.assertEquals(newGame.game().getBoard(), dbGame.game().getBoard());

        GameData updatedGame = new GameData(1, "MyMom", "MyDad", newGame.gameName(), newGame.game());
        GAME_DAO.updateGame(updatedGame);

        dbGame = GAME_DAO.getGame(1);

        Assertions.assertEquals(updatedGame, dbGame);
    }

    @Test
    @Order(7)
    public void updateGameNegative(){
        Assertions.assertThrows(DataAccessException.class,
                () -> GAME_DAO.updateGame(new GameData(100, null, null, "TestGame", new ChessGame())));
          Assertions.assertThrows(DataAccessException.class,
                () -> GAME_DAO.updateGame(new GameData(1, null, null, "TestGame", null)));
    }

    @Test
    @Order(8)
    public void clearPositive() throws DataAccessException {
        GAME_DAO.clear();

        Assertions.assertNull(GAME_DAO.getGame(1));
    }

    @Test
    @Order(10)
    public void listGameNegative() throws DataAccessException {
        Assertions.assertEquals(GAME_DAO.listGames(), List.of());
    }
}
