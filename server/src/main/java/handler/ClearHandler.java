package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.servieimplimentation.ClearService;

import java.util.Map;

public class ClearHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        String body;
        try {
            new ClearService().clear();
            body = new Gson().toJson(Map.of());
            context.status(200)
                    .json(body);
        } catch (DataAccessException ex){
            body = new Gson().toJson(Map.of("message", "Error: SQL Error: " + ex.getLocalizedMessage()));
            context.status(500)
                    .json(body);
        }
    }
}
