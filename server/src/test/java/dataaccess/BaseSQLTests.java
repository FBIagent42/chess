package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

public interface BaseSQLTests {
    AuthData newAuth = new AuthData("TestToken", "Bill");
    UserData newUser = new UserData("Bill", "myPass", "stuff@gmail.com");
    GameData newGame = new GameData(0, null, null, "TestGame", new ChessGame());

}
