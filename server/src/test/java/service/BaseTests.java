package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.servieImplimentation.ClearService;
import service.servieImplimentation.GameService;
import service.servieImplimentation.UserService;

public interface BaseTests {
    UserService userService = new UserService();
    GameService gameService = new GameService();
    ClearService clearService = new ClearService();
    UserDOA userDOA = new MemoryUserDOA();
    GameDOA gameDOA = new MemoryGameDOA();
    AuthDOA authDOA = new MemoryAuthDOA();

    default void addAuth(String authToken, String username){
        authDOA.createAuth(new AuthData(authToken, username));
    }

    default void addUser(String username, String password, String email){
        userDOA.createUser(new UserData(username, password, email));
    }

    default void addGame(int gameID, String gameName, ChessGame game){
        gameDOA.createGame(new GameData(gameID, null, null, gameName, game));
    }

}
