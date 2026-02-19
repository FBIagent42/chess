package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.JoinGameRequest;
import service.serviceExceptions.ColorTakenException;
import service.serviceExceptions.NoGameException;
import service.servieImplimentation.GameService;

import java.util.Map;

public class JoinGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        String authToken = context.header("authorization");
        var joinGameRequest = new Gson().fromJson(context.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(authToken, joinGameRequest.playerColor(), joinGameRequest.gameID());
        String body;
        int statusCode;

        try{
            new GameService().joinGame(joinGameRequest);
            body = new Gson().toJson(Map.of());
            statusCode = 200;
        } catch (NoGameException ex){
            body = new Gson().toJson(Map.of("message", "Error: No game with that ID exists."));
            statusCode = 404;
        } catch (ColorTakenException ex){
            body = new Gson().toJson(Map.of("message", "Error: Color already taken."));
            statusCode = 403;
        }

        context.status(statusCode)
                .json(body);
    }
}
