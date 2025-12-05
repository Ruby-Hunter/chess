package websocket.messages;

public class ServerNotificationMessage extends ServerMessage{

    public ServerNotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION, message);
    }
}
