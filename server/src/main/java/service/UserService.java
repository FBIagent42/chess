package service;

import dataaccess.MemoryAuthDOA;
import dataaccess.MemoryUserDOA;
import model.AuthData;
import model.UserData;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.resulsts.LoginResult;
import service.resulsts.RegisterResult;

public class UserService implements Service{
    private final MemoryUserDOA userDOA = new MemoryUserDOA();
    private final MemoryAuthDOA authDOA = new MemoryAuthDOA();

    public RegisterResult register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        UserData user =  userDOA.getUser(username);
        if(user != null){
            //throw(AlreadyTakenException());
        }

        userDOA.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));
        String authToken = generateToken();
        authDOA.createAuth(new AuthData(authToken, username));

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        UserData user =  userDOA.getUser(username);
        if(user == null){
            //throw(UserNotFoundException());
        }
        if(user.password() != loginRequest.password()){
            //throw(UnauthorizedException);
        }

        String authToken = generateToken();
        authDOA.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }
    public void logout(LogoutRequest logoutRequest) {
        String authToken = logoutRequest.authToken();
        if(authDOA.getAuth(authToken) == null){
            //throw(UnauthorizedException());
        }

        authDOA.deleteAuth(authToken);
    }
}
