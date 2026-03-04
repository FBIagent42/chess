package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTest implements BaseTests{

    @Test
    public void positiveClear() throws DataAccessException {
        addAuth("Test", "Test");
        addUser("Corbin", "Test", "Test");
        addGame("Test", new ChessGame());

        CLEAR_SERVICE.clear();

        AuthData authData = AUTH_DAO.getAuth("Test");
        GameData gameData = GAME_DAO.getGame(1234);
        UserData userData = USER_DAO.getUser("Corbin");

        Assertions.assertNull(authData);
        Assertions.assertNull(gameData);
        Assertions.assertNull(userData);
    }
}
