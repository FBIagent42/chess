package server.WebSocket;

import chess.ChessGame;
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
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void move(Session session, String username, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        int gameID = command.getGameID();
        ChessGame game = service.getGame(gameID);
        if(game.isGameOver()){
            var error = new ErrorMessage("Game is Over");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        ChessGame.TeamColor color = game.getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor();
        if(!color.toString().toLowerCase().equals(connections.sessions.get(session))){
            var error = new ErrorMessage("Not your piece");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        if(color != game.getTeamTurn()){
            var error = new ErrorMessage("Not your turn");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        try {
            service.makeMove(gameID, command.getMove());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (InvalidMoveException e) {
            var error = new ErrorMessage("Invalid Move");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        game = service.getGame(gameID);
        var message = String.format("%s played the move %s", username, command.getMove());
        var notification = new NotificationMessage(message);
        var loadGame = new LoadGameMessage(game);
        connections.broadcast(gameID, null, loadGame);
        connections.broadcast(gameID, session, notification);
        color = game.getTeamTurn();
        if(game.isInCheck(color)){
            if(game.isInCheckmate(color)){
                message = String.format("%s is in checkmate", color);
            } else {
                message = String.format("%s is in check", color);
            }
        } else if (game.isInStalemate(color)) {
            message = String.format("%s is in stalemate", color);
        } else{
            return;
        }
        notification = new NotificationMessage(message);
        connections.broadcast(gameID, null, notification);
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
        connections.add(gameID, session, color);
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

    private void resign(Session session, String username, UserGameCommand command) throws IOException, DataAccessException {
        if(Objects.equals(connections.sessions.get(session), "an observer")){
            var error = new ErrorMessage("Observer cannot resign");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        if(!service.gameOver(command.getGameID())){
            var error = new ErrorMessage("Game is already over");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        var message = String.format("%s resigned", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(),null, notification);
    }
}