package ui;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint{
    public Session session;
    private final Gson ser;
    Client client;

    public WebSocketFacade(String uriString, Client newClient) throws Exception {
        URI uri = new URI(uriString);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);
        ser = new Gson();
        this.client = newClient;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                var msg = ser.fromJson(message, ServerMessage.class);
                switch(msg.getServerMessageType()){
                    case NOTIFICATION -> {
                        ServerNotificationMessage notMsg = ser.fromJson(message, ServerNotificationMessage.class);
                        System.out.println(notMsg.getMessage());
                    }
                    case LOAD_GAME -> {
                        ServerLoadGameMessage gameMsg = ser.fromJson(message, ServerLoadGameMessage.class);
                        System.out.println("\n" + BoardPrinter.printBoard(gameMsg.getGame(), gameMsg.getColor()));
                        switch(client.getState()){
                            case PLAYING -> System.out.printf("\n[PLAYING: %s] >>> ", client.getColor());
                            case OBSERVING -> System.out.print("\n[OBSERVING] >>> ");
                        }
                    }
                    case ERROR -> {
                        ServerErrorMessage errMsg = ser.fromJson(message, ServerErrorMessage.class);
                        System.out.println("Error: " + errMsg.getErrorMessage());
                    }
                }
            }
        });
    }

    public void send(UserGameCommand cmd) throws IOException {
        String jsonCmd = new Gson().toJson(cmd);
        session.getBasicRemote().sendText(jsonCmd);
    }

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}


