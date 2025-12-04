package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.server.Authentication;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

public class WebSocketHandler {
    Gson ser = new Gson();

    public WebSocketHandler(){
    }

    public void handleMessage(WsMessageContext ctx){
        UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch(cmd.getCommandType()){
            case CONNECT -> {
                handleConnect(ctx, cmd);
            }
            case MAKE_MOVE -> {
                handleMove(ctx, cmd);
            }
            case LEAVE -> {
                handleLeave(ctx, cmd);
            }
            case RESIGN -> {
                handleResign(ctx, cmd);
            }
            default -> {
                echo(ctx);
            }
        }
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(new ServerNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "Notification")));
    }

    private void handleMove(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(ctx.message()));
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(ctx.message()));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(ctx.message()));
    }

    public void echo(WsMessageContext ctx){
        ctx.send("Echoing WebSocket response:" + ctx.message());
    }

    public void closeMessage(){
        System.err.println("Websocket closed");
    }
}
