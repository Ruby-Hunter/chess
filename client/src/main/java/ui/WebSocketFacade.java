package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerLoad_GameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
                    case NOTIFICATION -> System.out.println(msg.getMessage());
                    case LOAD_GAME -> {
                        ServerLoad_GameMessage gameMsg = ser.fromJson(message, ServerLoad_GameMessage.class);
                        System.out.println("\n" + gameMsg.getMessage());
                        System.out.println(BoardPrinter.printBoard(gameMsg.getGame(), gameMsg.getColor()));
                        switch(client.getState()){
                            case PLAYING -> System.out.printf("\n[PLAYING: %s] >>> ", client.getColor());
                            case OBSERVING -> System.out.print("\n[OBSERVING] >>> ");
                        }
                    }
                    case ERROR -> System.out.println("Error: " + msg.getMessage());
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


