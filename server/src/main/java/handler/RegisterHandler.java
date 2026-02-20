package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.RegisterRequest;
import service.resulsts.RegisterResult;
import service.serviceexceptions.AlreadyTakenException;
import service.servieimplimentation.UserService;

import java.util.Map;

public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        var registerRequest = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult registerResult;
        String body;

        if(registerRequest.username() == null
                || registerRequest.password() == null
                || registerRequest.email() == null){
            body = new Gson().toJson(Map.of("message", "Error: Bad request."));
            context.status(400)
                    .json(body);
            return;
        }

        try {
            registerResult = new UserService().register(registerRequest);
            body = new Gson().toJson(registerResult);
            context.status(200)
                    .json(body);
        } catch (AlreadyTakenException ex) {
            body = new Gson().toJson(Map.of("message", "Error: username already taken."));
            context.status(403)
                    .json(body);
        }
    }
}
