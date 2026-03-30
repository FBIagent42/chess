package client.main.websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}
