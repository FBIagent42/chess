package service.servieimplimentation;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.resulsts.LoginResult;
import service.resulsts.RegisterResult;
import service.serviceexceptions.*;

public class UserService extends Service{

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData user =  userDAO.getUser(username);
        if(user != null){
            throw(new AlreadyTakenException());
        }

        userDAO.createUser(new UserData(username, hashedPassword, registerRequest.email()));
        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));

        return new RegisterResult(username, authToken);
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        UserData user =  userDAO.getUser(username);
        if(user == null || !BCrypt.checkpw(loginRequest.password(), user.password())){
            throw(new UnauthorizedException());
        }

        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));

        return new LoginResult(username, authToken);
    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        verifyAuth(authToken);

        authDAO.deleteAuth(authToken);
    }
}
