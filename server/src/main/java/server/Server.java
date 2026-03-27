package server;

import handler.*;
import io.javalin.*;
import server.WebSocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        webSocketHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler())
                .post("/session", new LoginHandler())
                .delete("/session", new LogoutHandler())
                .get("/game", new ListGamesHandler())
                .post("/game", new CreateGameHandler())
                .put("/game", new JoinGameHandler())
                .delete("/db", new ClearHandler())
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
