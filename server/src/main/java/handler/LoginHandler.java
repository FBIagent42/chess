package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.LoginRequest;
import service.resulsts.LoginResult;
import service.serviceexceptions.UnauthorizedException;
import service.servieimplimentation.UserService;

import java.util.Map;

public class LoginHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        var loginRequest = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult loginResult;
        String body;

        if(loginRequest.username() == null || loginRequest.password() == null){
            body = new Gson().toJson(Map.of("message", "Error: Bad request."));
            context.status(400)
                    .json(body);
            return;
        }

        try{
            loginResult = new UserService().login(loginRequest);
            body = new Gson().toJson(loginResult);
            context.status(200)
                    .json(body);
        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Error: Unauthorized."));
            context.status(401)
                    .json(body);
        }
    }
}
