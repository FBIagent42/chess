package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.requests.ListGamesRequest;
import service.resulsts.ListGamesResult;
import service.serviceExceptions.UnauthorizedException;
import service.servieImplimentation.GameService;

import java.util.Map;

public class ListGamesHandler implements Handler{
    @Override
    public void handle(@NotNull Context context){
        var listGamesRequest = new Gson().fromJson(context.header("authorization"), ListGamesRequest.class);
        ListGamesResult listGamesResult;
        String body;
        int statusCode;

        try{
            listGamesResult = new GameService().listGames(listGamesRequest);
            body = new Gson().toJson(listGamesResult);
            statusCode = 200;
        } catch (UnauthorizedException ex){
            body = new Gson().toJson(Map.of("message", "Unauthorized."));
            statusCode = 401;
        }

        context.status(statusCode)
                .json(body);
    }
}
