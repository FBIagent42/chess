package service;

import dataaccess.MemoryAuthDOA;
import dataaccess.MemoryGameDOA;
import dataaccess.MemoryUserDOA;

import java.util.UUID;

public interface Service {
    MemoryUserDOA userDOA = new MemoryUserDOA();
    MemoryAuthDOA authDOA = new MemoryAuthDOA();
    MemoryGameDOA gameDoa = new MemoryGameDOA();

    default String generateToken() {
        return UUID.randomUUID().toString();
    }

    default void varifyAuth(String authToken){
        if(authDOA.getAuth(authToken) == null){
            //throw(UnauthorizedException());
        }
    }
}
