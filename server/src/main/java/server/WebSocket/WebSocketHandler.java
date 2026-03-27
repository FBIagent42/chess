package server.WebSocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.serviceexceptions.UnauthorizedException;
import service.servieimplimentation.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final GameService service = new GameService();
    private final ConnectionManager connections = new ConnectionManager();


    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        int gameID = -1;
        Session session = ctx.session;
        try {
            MakeMoveCommand command = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
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
        } catch (DataAccessException | InvalidMoveException e) {
            throw new RuntimeException(e);
        } catch (UnauthorizedException e){
            var error = new ErrorMessage("Unauthorized");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void move(Session session, String username, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        int gameID = command.getGameID();
        service.makeMove(gameID, command.getMove());
        var message = String.format("%s played the %s", username, command.getMove());
        var notification = new NotificationMessage(message);
        var loadGame = new LoadGameMessage(service.getGame(gameID));
        connections.broadcast(gameID, null, loadGame);
        connections.broadcast(gameID, session, notification);
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        int gameID = command.getGameID();
        String color;
        try {
            color = service.getColor(gameID, username);
        } catch (DataAccessException e) {
            var error = new ErrorMessage("No game with that ID");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        connections.add(gameID, session);
        var message = String.format("%s joined the game as %s", username, color);
        var notification = new NotificationMessage(message);
        var loadGame = new LoadGameMessage(service.getGame(gameID));
        session.getRemote().sendString(new Gson().toJson(loadGame));
        connections.broadcast(gameID, session, notification);
    }

    private void leave(Session session, String username, UserGameCommand command) throws IOException {
        connections.remove(command.getGameID(), session);
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), session, notification);
    }

    private void resign(Session session, String username, UserGameCommand command) throws IOException {
        connections.remove(command.getGameID(), session);
        var message = String.format("%s resigned", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(),null, notification);
    }
}