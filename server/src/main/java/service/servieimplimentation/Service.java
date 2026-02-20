package service.servieimplimentation;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import service.serviceexceptions.UnauthorizedException;

import java.util.UUID;

public interface Service {
    MemoryUserDAO USER_DAO = new MemoryUserDAO();
    MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    MemoryGameDAO GAME_DAO = new MemoryGameDAO();

    default String generateToken() {
        return UUID.randomUUID().toString();
    }

    default void varifyAuth(String authToken){
        if(AUTH_DAO.getAuth(authToken) == null){
            throw(new UnauthorizedException());
        }
    }
}
