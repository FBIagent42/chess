package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage() {
        super(ServerMessageType.ERROR);
    }
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: " + message;
    }
}
