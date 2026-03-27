package websocket.messages;

public class NotificationMessage extends ServerMessage{
    String message;
    public NotificationMessage() {
        super(ServerMessageType.NOTIFICATION);
    }

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
