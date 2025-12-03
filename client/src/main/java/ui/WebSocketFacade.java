package ui;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WebSocketFacade extends Endpoint{
    public Session session;

    public static void main(String[] args) throws Exception {
        WebSocketFacade client = new WebSocketFacade("299");

        Scanner scanner = new Scanner(System.in);

        while(true) {
            client.send(scanner.nextLine());
        }
    }

    public WebSocketFacade(String uriString) throws Exception {
        URI uri = new URI(uriString);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    // This method must be overridden, but we don't have to do anything with it
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}


