package ui;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{
    public Session session;


    public WebSocketFacade(String uriString) throws Exception {
        URI uri = new URI(uriString);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                var msg = new Gson().fromJson(message, ServerMessage.class);
                System.out.println(msg.getServerMessageType().toString());
            }
        });
    }

    public void send(ServerMessage msg) throws IOException {
        String jsonMsg = new Gson().toJson(msg);
        session.getBasicRemote().sendText(jsonMsg);
    }

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}


