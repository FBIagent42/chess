package service;

import dataaccess.*;
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
}
