package service.servieimplimentation;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.resulsts.LoginResult;
import service.resulsts.RegisterResult;
import service.serviceexceptions.*;

public class UserService extends Service{

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        UserData user =  USER_DAO.getUser(username);
        if(user != null){
            throw(new AlreadyTakenException());
        }

        USER_DAO.createUser(new UserData(username, registerRequest.password(), registerRequest.email()));
        String authToken = generateToken();
        AUTH_DAO.createAuth(new AuthData(authToken, username));

        return new RegisterResult(username, authToken);
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        UserData user =  USER_DAO.getUser(username);
        if(user == null || !user.password().equals(loginRequest.password())){
            throw(new UnauthorizedException());
        }

        String authToken = generateToken();
        AUTH_DAO.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        verifyAuth(authToken);

        AUTH_DAO.deleteAuth(authToken);
    }
}
