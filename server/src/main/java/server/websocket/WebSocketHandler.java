package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.GameData;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.server.Authentication;
import org.glassfish.grizzly.utils.Pair;
import service.UserService;
import websocket.commands.UserConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoad_GameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;


public class WebSocketHandler {
    Gson ser;
    DataAccess dataAccess;
    HashMap<Integer, HashMap<String, WsMessageContext>> gameParticipants;

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

    private void handleConnect(WsMessageContext ctx, UserConnectCommand cmd) {
        try {
            int gID = cmd.getGameID();
            GameData gData = dataAccess.getGame(gID);
            String uName = dataAccess.getAuth(cmd.getAuthToken()).username();

            if(gameParticipants.get(gID) != null && gameParticipants.get(gID).containsKey(uName)){
                ctx.send(ser.toJson(new ServerErrorMessage("user already in game")));
            }
            if (cmd.getColor() == ChessGame.TeamColor.WHITE) { // Join as White
                dataAccess.updateGame(new GameData(gID, uName,
                        gData.blackUsername(), gData.gameName(), gData.game()));
                gameParticipants.computeIfAbsent(gID, k -> new HashMap<>());
                gameParticipants.get(gID).put(uName, ctx);
                ctx.send(ser.toJson(new ServerLoad_GameMessage("Websocket Connected",
                        gData.game(), ChessGame.TeamColor.WHITE)));
            } else if (cmd.getColor() == ChessGame.TeamColor.BLACK) { // Join as black
                dataAccess.updateGame(new GameData(gID, gData.whiteUsername(),
                        uName, gData.gameName(), gData.game()));
                gameParticipants.computeIfAbsent(gID, k -> new HashMap<>());
                gameParticipants.get(gID).put(uName, ctx);
                ctx.send(ser.toJson(new ServerLoad_GameMessage("Websocket Connected",
                        gData.game(), ChessGame.TeamColor.BLACK)));
            } else { // Observing
                gameParticipants.computeIfAbsent(gID, k -> new HashMap<>());
                gameParticipants.get(gID).put(uName, ctx);
                ctx.send(ser.toJson(new ServerLoad_GameMessage("Websocket Connected",
                        gData.game(), ChessGame.TeamColor.WHITE)));
                return; // ensures observer joining does not notify players
            }
            gameParticipants.get(gID).forEach((_, curCtx) -> {
                curCtx.send(ser.toJson(new ServerNotificationMessage(
                        "Player " + uName + " has joined the game as " + cmd.getColor())));
            });
        } catch (Exception e) {
            ctx.send(ser.toJson(new ServerErrorMessage("Error Connecting")));
        }
    }

    private void handleMove(WsMessageContext ctx, UserMoveCommand cmd){
        try {
            GameData gData = dataAccess.getGame(cmd.getGameID());
            String uName = dataAccess.getAuth(cmd.getAuthToken()).username();

            gData.game().makeMove(cmd.getMove());
            dataAccess.updateGame(gData);
            gameParticipants.get(cmd.getGameID()).forEach((curName, curCtx) -> {
                ChessGame.TeamColor color = (Objects.equals(gData.blackUsername(), curName))
                        ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                if(curName.equals(uName)) {
                    curCtx.send(ser.toJson(new ServerLoad_GameMessage("Move valid ", gData.game(), color)));
                } else{
                    curCtx.send(ser.toJson(new ServerLoad_GameMessage("Game loaded: ", gData.game(), color)));
                }
            });
        } catch (InvalidMoveException ex){
            ctx.send(ser.toJson(new ServerErrorMessage("Move is invalid")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand cmd){
        try {
            int gID = cmd.getGameID();
            GameData gData = dataAccess.getGame(gID);
            String uName = dataAccess.getAuth(cmd.getAuthToken()).username();

            if(gameParticipants.get(gID) == null){
                ctx.send(ser.toJson(new ServerErrorMessage("game " + gData.gameName() + " does not exist")));
            }
            if(!gameParticipants.get(gID).get(uName).equals(ctx)){
                ctx.send(ser.toJson(new ServerErrorMessage("user not in game"))); return;
            }
            if (Objects.equals(gData.whiteUsername(), uName)) { // White
                dataAccess.updateGame(new GameData(gID, null,
                        gData.blackUsername(), gData.gameName(), gData.game()));
                gameParticipants.get(gID).remove(uName);
            } else if (Objects.equals(gData.blackUsername(), uName)) { // Black
                dataAccess.updateGame(new GameData(gID, gData.whiteUsername(),
                        null, gData.gameName(), gData.game()));
                gameParticipants.get(gID).remove(uName);
            } else{ // Observer
                gameParticipants.get(gID).remove(uName);
                return; // Observer leaving shouldn't send notification to all
            }
            ctx.send(ser.toJson(new ServerNotificationMessage("Left Game " + gData.gameName())));
            gameParticipants.get(gID).forEach((_, curCtx) -> {
                curCtx.send(ser.toJson(new ServerNotificationMessage(
                        "Player " + gData.gameName() + " has left the game.")));
            });
        } catch (Exception e) {
            ctx.send(ser.toJson(new ServerErrorMessage("Error Leaving")));
        }
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

    public void kickOutPlayers(){
        try {
            var gamesList = dataAccess.listGames();
            gamesList.forEach(gData -> {
                try {
                    dataAccess.updateGame(new GameData(gData.gameID(), null,
                            null, gData.gameName(), gData.game()));
                } catch (Exception ex){
                    System.err.println("Fatal error with kicking out players: " + ex.getMessage());
                }
            });
        } catch (Exception ex){
            System.err.println("Couldn't get game list: " + ex.getMessage());
        }

    }
}
