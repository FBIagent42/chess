package service;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTest implements BaseTests{

    @Test
    public void positiveClear(){
        clearService.clear();

        AuthData authData = authDOA.getAuth("Test");
        GameData gameData = gameDOA.getGame(1234);
        UserData userData = userDOA.getUser("Corbin");

        Assertions.assertNull(authData);
        Assertions.assertNull(gameData);
        Assertions.assertNull(userData);
    }
}
