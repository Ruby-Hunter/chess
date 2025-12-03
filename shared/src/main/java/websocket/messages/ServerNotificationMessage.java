package websocket.messages;

public class ServerNotificationMessage extends ServerMessage{

    public ServerNotificationMessage(ServerMessageType type, String message) {
        super(type, message);
    }
}
