package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.CreateGameRequest;
import service.resulsts.CreateGameResult;
import service.serviceexceptions.UnauthorizedException;
import service.servieimplimentation.GameService;

import java.util.Map;

public class CreateGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        String authToken = context.header("authorization");
        var createGameRequest = new Gson().fromJson(context.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(createGameRequest.gameName(), authToken);
        CreateGameResult createGameResult;
        String body;

        if(createGameRequest.gameName() == null){
            body = new Gson().toJson(Map.of("message", "Error: Bad request."));
            context.status(400)
                    .json(body);
            return;
        }

        try{
            createGameResult = new GameService().createGame(createGameRequest);
            body = new Gson().toJson(createGameResult);
            context.status(200)
                    .json(body);

        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Error: Unauthorized."));
            context.status(401)
                    .json(body);
        }
    }
}
