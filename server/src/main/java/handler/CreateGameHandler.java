package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.CreateGameRequest;
import service.requests.ListGamesRequest;
import service.resulsts.CreateGameResult;
import service.resulsts.ListGamesResult;
import service.serviceExceptions.UnauthorizedException;
import service.servieImplimentation.GameService;

import java.util.Map;

public class CreateGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        String authToken = context.header("authorization");
        var createGameRequest    = new Gson().fromJson(context.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(createGameRequest.gameName(), authToken);
        CreateGameResult createGameResult;
        String body;
        int statusCode;

        try{
            createGameResult = new GameService().createGame(createGameRequest);
            body = new Gson().toJson(createGameResult);
            statusCode = 200;
        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Unauthorized."));
            statusCode = 401;
        }

        context.status(statusCode)
                .json(body);
    }
}
