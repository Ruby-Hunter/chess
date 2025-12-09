package websocket.messages;

public class ServerErrorMessage extends ServerMessage{
    String errorMessage;

    public ServerErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        errorMessage = message;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
