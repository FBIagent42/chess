package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.servieimplimentation.ClearService;

import java.util.Map;

public class ClearHandler implements Handler {
    @Override
    public void handle(@NotNull Context context){
        new ClearService().clear();
        var body = new Gson().toJson(Map.of());
        context.status(200)
                .json(body);
    }
}
