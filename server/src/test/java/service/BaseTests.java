package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import service.servieimplimentation.ClearService;
import service.servieimplimentation.GameService;
import service.servieimplimentation.Service;
import service.servieimplimentation.UserService;

public interface BaseTests {
    UserDAO USER_DAO = new MemoryUserDAO();
    GameDAO GAME_DAO = new MemoryGameDAO();
    AuthDAO AUTH_DAO = new MemoryAuthDAO();
    Service SERVICE = new Service();
    UserService USER_SERVICE = new UserService();
    GameService GAME_SERVICE = new GameService();
    ClearService CLEAR_SERVICE = new ClearService();

    @BeforeAll
    static void setService(){
        SERVICE.setDaos(USER_DAO, AUTH_DAO, GAME_DAO);
    }


    default void addAuth(String authToken, String username) throws DataAccessException {
        AUTH_DAO.createAuth(new AuthData(authToken, username));
    }

    default void addUser(String username, String password, String email) throws DataAccessException {
        USER_DAO.createUser(new UserData(username, password, email));
    }

    default int addGame(String gameName, ChessGame game) throws DataAccessException {
        return GAME_DAO.createGame(new GameData(0, null, null, gameName, game));
    }

}
