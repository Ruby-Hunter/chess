package websocket.messages;

public class ServerErrorMessage extends ServerMessage{

    public ServerErrorMessage(String message) {
        super(ServerMessageType.ERROR, message);
    }
}
