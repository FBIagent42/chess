package server.WebSocket;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.servieimplimentation.GameService;
import service.servieimplimentation.Service;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final GameService service = new Service();
    private final ConnectionManager connections = new ConnectionManager();
    protected static UserDAO userDAO;
    protected static AuthDAO authDAO;
    protected static GameDAO gameDAO;


    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        int gameID = -1;
        Session session = ctx.session;
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
            String username = service.verifyAuth(command.getAuthToken());
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.session, username, command);
                case MAKE_MOVE -> move(ctx.session, username, command);
                case LEAVE -> leave(ctx.session, username, command);
                case RESIGN -> resign(ctx.session, username, command);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void move(Session session, String username, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        service.makeMove(command.getGameID(), command.getMove());
        var message = String.format("%s played the %s", username, command.getMove());
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), notification);
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        connections.add(command.getGameID(), session);
        String color;
        if(Objects.equals(gameDAO.getGame(command.getGameID()).whiteUsername(), username)){
            color = "white";
        } else if (Objects.equals(gameDAO.getGame(command.getGameID()).blackUsername(), username)) {
            color = "black";
        } else {
            color = "an observer";
        }
        var message = String.format("%s joined the game as %s", username, color);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), notification);
    }

    private void leave(Session session, String username, UserGameCommand command) throws IOException {
        connections.remove(command.getGameID(), session);
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), notification);
    }

    private void resign(Session session, String username, UserGameCommand command) throws IOException {
        connections.remove(command.getGameID(), session);
        var message = String.format("%s resigned", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.getGameID(), notification);
    }
}