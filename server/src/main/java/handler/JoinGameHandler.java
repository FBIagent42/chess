package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.JoinGameRequest;
import service.serviceExceptions.ColorTakenException;
import service.serviceExceptions.NoGameException;
import service.serviceExceptions.UnauthorizedException;
import service.servieImplimentation.GameService;

import java.util.Map;

public class JoinGameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        String authToken = context.header("authorization");
        var joinGameRequest = new Gson().fromJson(context.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(authToken, joinGameRequest.playerColor(), joinGameRequest.gameID());
        String body;

        if(joinGameRequest.playerColor() == null
                ||!(joinGameRequest.playerColor().equals("WHITE") || joinGameRequest.playerColor().equals("BLACK"))
                || joinGameRequest.gameID() == 0){
            body = new Gson().toJson(Map.of("message", "Error: Bad request."));
            context.status(400)
                    .json(body);
            return;
        }

        try{
            new GameService().joinGame(joinGameRequest);
            body = new Gson().toJson(Map.of());
            context.status(200)
                    .json(body);
        } catch (NoGameException ex){
            body = new Gson().toJson(Map.of("message", "Error: No game with that ID exists."));
            context.status(404)
                    .json(body);
        } catch (ColorTakenException ex){
            body = new Gson().toJson(Map.of("message", "Error: Color already taken."));
            context.status(403)
                    .json(body);
        }  catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Error: Unauthorized."));
            context.status(401)
                    .json(body);
        }
    }
}
