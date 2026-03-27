package service.servieimplimentation;

import dataaccess.*;
import service.serviceexceptions.UnauthorizedException;

import java.util.UUID;

public class Service {
    protected static UserDAO userDAO;
    protected static AuthDAO authDAO;
    protected static GameDAO gameDAO;

    static {
        try{
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException ex){
            throw new RuntimeException("Unable to connect to server");
        }
    }

    public void setDaos(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        userDAO = userDao;
        authDAO = authDao;
        gameDAO = gameDao;
    }

    String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String verifyAuth(String authToken) throws UnauthorizedException, DataAccessException {
        if(authDAO.getAuth(authToken) == null){
            throw(new UnauthorizedException());
        }
        return authDAO.getAuth(authToken).username();
    }
}
