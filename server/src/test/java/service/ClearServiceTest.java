package service;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTest implements BaseTests{

    @Test
    public void positiveClear(){
        addAuth("Test", "Test");
        addUser("Corbin", "Test", "Test");
        addGame(1234, "Test", new ChessGame());

        clearService.clear();

        AuthData authData = authDOA.getAuth("Test");
        GameData gameData = gameDOA.getGame(1234);
        UserData userData = userDOA.getUser("Corbin");

        Assertions.assertNull(authData);
        Assertions.assertNull(gameData);
        Assertions.assertNull(userData);
    }
}
