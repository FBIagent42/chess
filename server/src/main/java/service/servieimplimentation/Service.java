package service.servieimplimentation;

import dataaccess.*;
import service.serviceexceptions.UnauthorizedException;

import java.util.UUID;

public interface Service {
    UserDAO USER_DAO = new MemoryUserDAO();
    AuthDAO AUTH_DAO = new MemoryAuthDAO();
    GameDAO GAME_DAO = new MemoryGameDAO();

    default String generateToken() {
        return UUID.randomUUID().toString();
    }

    default void varifyAuth(String authToken){
        if(AUTH_DAO.getAuth(authToken) == null){
            throw(new UnauthorizedException());
        }
    }
}
