package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.LogoutRequest;
import service.serviceexceptions.UnauthorizedException;
import service.servieimplimentation.UserService;

import java.util.Map;

public class LogoutHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        var logoutRequest = new LogoutRequest(context.header("authorization"));
        String body;

        try{
            new UserService().logout(logoutRequest);
            body = new Gson().toJson(Map.of());
            context.status(200)
                    .json(body);
        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Error: Unauthorized."));
            context.status(401)
                    .json(body);
        }
    }
}
