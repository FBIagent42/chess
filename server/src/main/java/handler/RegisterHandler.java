package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.RegisterRequest;
import service.resulsts.RegisterResult;
import service.serviceExceptions.AlreadyTakenException;
import service.servieImplimentation.UserService;

import java.util.Map;

public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        var registerRequest = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult registerResult;
        String body;
        int statusCode;

        try {
            registerResult = new UserService().register(registerRequest);
            body = new Gson().toJson(registerResult);
            statusCode = 200;
        } catch (AlreadyTakenException ex) {
            body = new Gson().toJson(Map.of("message", "Error: username already taken."));
            statusCode = 403;
        }

        context.status(statusCode)
                .json(body);
    }
}
