package service.servieImplimentation;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import service.serviceExceptions.UnauthorizedException;

import java.util.UUID;

public interface Service {
    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();
    MemoryGameDAO gameDAO = new MemoryGameDAO();

    default String generateToken() {
        return UUID.randomUUID().toString();
    }

    default void varifyAuth(String authToken){
        if(authDAO.getAuth(authToken) == null){
            throw(new UnauthorizedException());
        }
    }
}
