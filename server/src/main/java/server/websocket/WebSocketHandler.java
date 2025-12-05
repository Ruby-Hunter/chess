package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.GameData;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerLoad_GameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

public class WebSocketHandler {
    Gson ser;
    DataAccess dataAccess;

    public WebSocketHandler(DataAccess access){
        ser = new Gson();
        dataAccess = access;
    }

    public void handleMessage(WsMessageContext ctx) {
        UserGameCommand cmd = ser.fromJson(ctx.message(), UserGameCommand.class);
        switch(cmd.getCommandType()){
            case CONNECT -> {
                handleConnect(ctx, cmd);
            }
            case MAKE_MOVE -> {
                UserMoveCommand moveCmd = ser.fromJson(ctx.message(), UserMoveCommand.class);
                handleMove(ctx, moveCmd);
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

    private void handleConnect(WsMessageContext ctx, UserGameCommand cmd) throws Exception {
        GameData gData = dataAccess.getGame(cmd.getGameID());
//        dataAccess.updateGame(new GameData(cmd.getGameID(), ));
        ctx.send(ser.toJson(new ServerNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "Websocket Connected")));
    }

    private void handleMove(WsMessageContext ctx, UserMoveCommand cmd){

//        dataAccess.updateGame(new GameData(cmd.getGameID(), ));
//        ctx.send(ser.toJson(new ServerLoad_GameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
//                "Game loaded", )));
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(new ServerNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "Notification")));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(new ServerNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                "Notification")));
    }

    public void echo(WsMessageContext ctx){
        ctx.send("Echoing WebSocket response:" + ctx.message());
    }

    public void closeMessage(){
        System.err.println("Websocket closed");
    }
}
