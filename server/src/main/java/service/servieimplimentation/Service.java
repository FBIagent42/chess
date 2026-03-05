package service.servieimplimentation;

import dataaccess.*;
import service.serviceexceptions.UnauthorizedException;

import java.util.UUID;

public class Service {
    protected static UserDAO USER_DAO;
    protected static AuthDAO AUTH_DAO;
    protected static GameDAO GAME_DAO;

    static {
        try{
            USER_DAO = new SQLUserDAO();
            AUTH_DAO = new SQLAuthDAO();
            GAME_DAO = new SQLGameDAO();
        } catch (DataAccessException ex){
            throw new RuntimeException("Unable to connect to server");
        }
    }

    public void setDaos(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        USER_DAO = userDao;
        AUTH_DAO = authDao;
        GAME_DAO = gameDao;
    }

    String generateToken() {
        return UUID.randomUUID().toString();
    }

    void verifyAuth(String authToken) throws DataAccessException {
        if(AUTH_DAO.getAuth(authToken) == null){
            throw(new UnauthorizedException());
        }
    }
}
