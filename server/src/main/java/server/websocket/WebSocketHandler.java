package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.GameData;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import websocket.commands.UserConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoad_GameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

import java.util.HashMap;
import java.util.HashSet;

public class WebSocketHandler {
    Gson ser;
    DataAccess dataAccess;
    HashMap<Integer, HashSet<String>> gameParticipants;

    public WebSocketHandler(DataAccess access){
        ser = new Gson();
        dataAccess = access;
        gameParticipants = new HashMap<>();
    }

    public void handleMessage(WsMessageContext ctx) throws Exception {
        UserGameCommand cmd = ser.fromJson(ctx.message(), UserGameCommand.class);
        switch(cmd.getCommandType()){
            case CONNECT -> {
                UserConnectCommand connCmd = ser.fromJson(ctx.message(), UserConnectCommand.class);
                handleConnect(ctx, connCmd);
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

    private void handleConnect(WsMessageContext ctx, UserConnectCommand cmd) throws Exception {
        try {
            int gID = cmd.getGameID();
            GameData gData = dataAccess.getGame(gID);
            if (cmd.getColor() == ChessGame.TeamColor.WHITE) { // Join as White
                if (gData.whiteUsername() == null) {
                    String uName = dataAccess.getAuth(cmd.getAuthToken()).username();
                    dataAccess.updateGame(new GameData(gID, uName,
                            gData.blackUsername(), gData.gameName(), gData.game()));
                    if(gameParticipants.get(gID) == null){
                        gameParticipants.put(gID, new HashSet<>().add);
                    }
                } else {
                    ctx.send(ser.toJson(new ServerErrorMessage("White already taken")));
                }
            } else if (cmd.getColor() == ChessGame.TeamColor.BLACK) { // Join as black
                if (gData.blackUsername() == null) {
                    String uName = dataAccess.getAuth(cmd.getAuthToken()).username();
                    dataAccess.updateGame(new GameData(gID, gData.whiteUsername(),
                            uName, gData.gameName(), gData.game()));
                } else {
                    ctx.send(ser.toJson(new ServerErrorMessage("Black already taken")));
                }
            } else { // Observing

            }
            //        dataAccess.updateGame(new GameData(cmd.getGameID(), ));
            ctx.send(ser.toJson(new ServerNotificationMessage("Websocket Connected")));
        } catch (Exception e) {
            ctx.send(ser.toJson(new ServerErrorMessage("Error Connecting")));
        }
    }

    private void handleMove(WsMessageContext ctx, UserMoveCommand cmd){
        try {
            GameData gData = dataAccess.getGame(cmd.getGameID());
            //        gData.game().makeMove(cmd.get)
            //        dataAccess.updateGame(new GameData(cmd.getGameID(), ));
            //        ctx.send(ser.toJson(new ServerLoad_GameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
            //                "Game loaded", )));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(new ServerNotificationMessage("Notification")));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand cmd){
        ctx.send(ser.toJson(new ServerNotificationMessage("Notification")));
    }

    public void echo(WsMessageContext ctx){
        ctx.send("Echoing WebSocket response:" + ctx.message());
    }

    public void closeMessage(){
        System.err.println("Websocket closed");
    }
}
