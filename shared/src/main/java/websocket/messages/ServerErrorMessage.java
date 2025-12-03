package websocket.messages;

public class ServerErrorMessage extends ServerMessage{

    public ServerErrorMessage(ServerMessageType type, String message) {
        super(type, message);
    }
}
