package service;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.resulsts.LoginResult;
import service.resulsts.RegisterResult;
import service.serviceExceptions.AlreadyTakenException;
import service.serviceExceptions.UnauthorizedException;
import service.serviceExceptions.UserNotFoundException;

public class UserServiceTests implements BaseTests{

    @AfterEach
    public void clearAll(){
        gameDOA.clear();
        userDOA.clear();
        authDOA.clear();
    }

    @Test
    public void positiveRegister(){
        String username = "Corbin C";
        String password = "1234";
        String email = "test@gmail.com";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);

        RegisterResult registerResult =  userService.register(registerRequest);

        //Assert that the Result class is correct
        Assertions.assertEquals(username, registerResult.username());
        Assertions.assertNotNull(registerResult.authToken());

        //Assert that the user is in the db
        UserData user = userDOA.getUser(username);
        Assertions.assertNotNull(user);

        //Assert that the data in the db is correct
        Assertions.assertEquals(username, user.username());
        Assertions.assertEquals(password, user.password());
        Assertions.assertEquals(email, user.email());
    }

    @Test
    public void negativeRegister(){
        String username = "Corbin";
        String password = "1234";
        String email = "test@gmail.com";
        addUser(username, password, email);
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);

        Assertions.assertThrows(AlreadyTakenException.class,
                () ->  userService.register(registerRequest));
    }

    @Test
    public void positiveLogin(){
        String username = "Corbin";
        String password = "1234";
        addUser(username, password, "Test");
        LoginRequest loginRequest = new LoginRequest(username, password);

        LoginResult loginResult = userService.login(loginRequest);

        //Assert that the Result class is correct
        Assertions.assertEquals(username, loginResult.username());
        Assertions.assertNotNull(loginResult.authToken());

        //Assert that the AuthToken is in the db
        AuthData authData = authDOA.getAuth(loginResult.authToken());
        Assertions.assertNotNull(authData);

        //Assert that the data in the db is correct
        Assertions.assertEquals(authData.authToken(), loginResult.authToken());
        Assertions.assertEquals(username, authData.username());
    }

    @Test
    public void negativeLoginNoUser(){
        String username = "Corbin";
        String password = "1234";
        LoginRequest loginRequest = new LoginRequest(username, password);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.login(loginRequest));
    }

    @Test
    public void negativeLoginWrongPassword(){
        String username = "Corbin";
        String password = "1";
        addUser(username, "Different", "Test");
        LoginRequest loginRequest = new LoginRequest(username, password);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.login(loginRequest));
    }

    @Test
    public void positiveLogout(){
        String authToken = "1111";
        addAuth(authToken, "Test");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        userService.logout(logoutRequest);

        Assertions.assertNull(authDOA.getAuth(authToken));
    }

    @Test
    public void negativeLogout(){
        String authToken = "Wrong";
        addAuth("Right", "Test");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.logout(logoutRequest));
    }
}
