package client.main;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ResponseException extends Exception {


    final private int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static String fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = "";
        if(map.get("message") != null){
            message = map.get("message").toString();
        } else if(map.get("title") != null && map.get("details") != null){
            message = map.get("title") + ": " + map.get("details");
        }
        return message;
    }

    public static ResponseException printCode(ResponseException e){
        return new ResponseException(e.code(), SET_TEXT_COLOR_RED + e.getMessage() + "\n");
    }

    public int code() {
        return code;
    }
}
