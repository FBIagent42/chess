package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.LoginRequest;
import service.resulsts.LoginResult;
import service.serviceExceptions.UnauthorizedException;
import service.serviceExceptions.UserNotFoundException;
import service.servieImplimentation.UserService;

import java.util.Map;

public class LoginHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        var loginRequest = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult loginResult;
        String body;
        int statusCode;

        try{
            loginResult = new UserService().login(loginRequest);
            body = new Gson().toJson(loginResult);
            statusCode = 200;
        } catch (UserNotFoundException ex){
            body = new Gson().toJson(Map.of("message", "Error: user not found."));
            statusCode = 404;
        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Unauthorized."));
            statusCode = 401;
        }

        context.status(statusCode)
                .json(body);
    }
}
