package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import websocket.messages.ServerMessage;

public class WebSocketHandler {

    public WebSocketHandler(){
    }

    public void handleMessage(WsMessageContext ctx){
        var msg = new Gson().fromJson(ctx.message(), ServerMessage.class);
        switch(msg.getServerMessageType()){
            case NOTIFICATION -> {
                handleNotification(ctx);
            }
            case ERROR -> {
                handleError(ctx);
            }
            case LOAD_GAME -> {
                handleLoadGame(ctx);
            }
            default -> {
                echo(ctx);
            }
        }
    }

    private void handleNotification(WsMessageContext ctx){
        ctx.send(new Gson().toJson(ctx.message()));
    }

    private void handleError(WsMessageContext ctx){
        ctx.send(new Gson().toJson(ctx.message()));
    }

    private void handleLoadGame(WsMessageContext ctx){
        ctx.send(new Gson().toJson(ctx.message()));
    }

    public void echo(WsMessageContext ctx){
        ctx.send("Echoing WebSocket response:" + ctx.message());
    }

    public void closeMessage(){
        System.err.println("Websocket closed");
    }
}
