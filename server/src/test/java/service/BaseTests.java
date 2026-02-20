package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.servieimplimentation.ClearService;
import service.servieimplimentation.GameService;
import service.servieimplimentation.UserService;

public interface BaseTests {
    UserService USER_SERVICE = new UserService();
    GameService GAME_SERVICE = new GameService();
    ClearService CLEAR_SERVICE = new ClearService();
    UserDAO USER_DAO = new MemoryUserDAO();
    GameDAO GAME_DAO = new MemoryGameDAO();
    AuthDAO AUTH_DAO = new MemoryAuthDAO();

    default void addAuth(String authToken, String username){
        AUTH_DAO.createAuth(new AuthData(authToken, username));
    }

    default void addUser(String username, String password, String email){
        USER_DAO.createUser(new UserData(username, password, email));
    }

    default void addGame(int gameID, String gameName, ChessGame game){
        GAME_DAO.createGame(new GameData(gameID, null, null, gameName, game));
    }

}
