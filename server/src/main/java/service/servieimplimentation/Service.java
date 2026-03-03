package service.servieimplimentation;

import dataaccess.*;
import service.serviceexceptions.UnauthorizedException;

import java.util.UUID;

public class Service {
    protected static UserDAO USER_DAO = new MemoryUserDAO();
    protected static AuthDAO AUTH_DAO = new MemoryAuthDAO();
    protected static GameDAO GAME_DAO = new MemoryGameDAO();

    public void setDaos(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        USER_DAO = userDao;
        AUTH_DAO = authDao;
        GAME_DAO = gameDao;
    }

    String generateToken() {
        return UUID.randomUUID().toString();
    }

    void verifyAuth(String authToken){
        if(AUTH_DAO.getAuth(authToken) == null){
            throw(new UnauthorizedException());
        }
    }
}
