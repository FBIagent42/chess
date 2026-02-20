package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.ListGamesRequest;
import service.resulsts.ListGamesResult;
import service.serviceexceptions.UnauthorizedException;
import service.servieimplimentation.GameService;

import java.util.Map;

public class ListGamesHandler implements Handler{
    @Override
    public void handle(@NotNull Context context){
        var listGamesRequest = new ListGamesRequest(context.header("authorization"));
        ListGamesResult listGamesResult;
        String body;

        try{
            listGamesResult = new GameService().listGames(listGamesRequest);
            body = new Gson().toJson(listGamesResult);
            context.status(200)
                    .json(body);
        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Error: Unauthorized."));
            context.status(401)
                    .json(body);
        }
    }
}
