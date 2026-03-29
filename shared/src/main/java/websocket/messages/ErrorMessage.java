package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: " + message;
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
