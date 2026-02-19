package service.servieImplimentation;

import model.AuthData;
import model.UserData;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.resulsts.LoginResult;
import service.resulsts.RegisterResult;
import service.serviceExceptions.*;

public class UserService implements Service{

    public RegisterResult register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        UserData user =  userDAO.getUser(username);
        if(user != null){
            throw(new AlreadyTakenException());
        }

        userDAO.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));
        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));

        return new RegisterResult(username, authToken);
    }
    public LoginResult login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        UserData user =  userDAO.getUser(username);
        if(user == null){
            throw(new UserNotFoundException());
        }
        if(!user.password().equals(loginRequest.password())){
            throw(new UnauthorizedException());
        }

        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }
    public void logout(LogoutRequest logoutRequest) {
        String authToken = logoutRequest.authToken();
        varifyAuth(authToken);

        authDAO.deleteAuth(authToken);
    }
}
