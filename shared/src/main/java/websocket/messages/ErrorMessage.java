package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String message;
    public ErrorMessage(ServerMessageType type) {
        super(type);
    }
    public ErrorMessage(ServerMessageType type, String message) {
        super(type);
        this.message = "Error: " + message;
    }
}
