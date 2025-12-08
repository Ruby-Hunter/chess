package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.SqlDataAccess;
import datamodel.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import server.websocket.WebSocketHandler;
import service.UserService;

import java.util.EventListener;
import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userServ;
    private WebSocketHandler wsHandler;

    public Server() {
        DataAccess dataAccess = new SqlDataAccess();
        userServ = new UserService(dataAccess);
        wsHandler = new WebSocketHandler(dataAccess);
        wsHandler.kickOutPlayers(); // to make sure there aren't players in the games before it starts
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.events(evLis -> {
            evLis.serverStopped(this::safeShutdown);
        });
        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> clearData(ctx));
        server.post("user", ctx -> register(ctx));
        server.post("session", ctx -> login(ctx));
        server.delete("session", ctx -> logout(ctx));
        server.get("game", ctx -> listGames(ctx));
        server.post("game", ctx -> createGame(ctx));
        server.put("game", ctx -> joinGame(ctx));
        server.ws("ws", ws -> {
            ws.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.err.println("Websocket connected");
            });
            ws.onMessage(ctx -> wsHandler.handleMessage(ctx));
            ws.onClose(_ -> wsHandler.closeMessage());
        });
    }

    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userServ.register(user);

            ctx.result(serializer.toJson(authData));
        } catch (AlreadyTakenException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(403).result(msg);
        } catch (BadRequestException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void login(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var login = serializer.fromJson(reqJson, LoginData.class);
            var authData = userServ.login(login);
            ctx.result(serializer.toJson(authData));
        } catch (BadRequestException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch(UnauthorizedException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void logout(Context ctx){
        try {
            var serializer = new Gson();
            String auth = ctx.header("authorization");
            userServ.logout(auth);
            ctx.result(serializer.toJson(null));
        } catch(UnauthorizedException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void listGames(Context ctx){
        try{
            var serializer = new Gson();
            String auth = ctx.header("authorization");
            var gamesList = userServ.listGames(auth);
            var listGamesRes = Map.of("games", gamesList);
            ctx.result(serializer.toJson(listGamesRes));
        } catch(UnauthorizedException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void createGame(Context ctx){
        try{
            var serializer = new Gson();
            String auth = ctx.header("authorization");
            String reqJsonBody = ctx.body();
            Map gameMap = serializer.fromJson(reqJsonBody, Map.class);
            String gameName = (String)gameMap.get("gameName");
            int gameID = userServ.createGame(auth, gameName);
            var createGameResult = Map.of("gameID", gameID);
            ctx.result(serializer.toJson(createGameResult));
        } catch (BadRequestException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch(UnauthorizedException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void joinGame(Context ctx){
        try{
            var serializer = new Gson();
            String auth = ctx.header("authorization");
            String reqJsonBody = ctx.body();
            JoinData joinData = serializer.fromJson(reqJsonBody, JoinData.class);
            userServ.joinGame(new JoinRequest(auth, joinData));
            ctx.result(serializer.toJson(null));
        } catch (BadRequestException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(400).result(msg);
        } catch(UnauthorizedException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(401).result(msg);
        } catch (AlreadyTakenException ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(403).result(msg);
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    private void clearData(Context ctx){
        try{
            var serializer = new Gson();
            userServ.clear();
            ctx.result(serializer.toJson(null));
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(500).result(msg);
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }

    private void safeShutdown(){
        wsHandler.kickOutPlayers();
        System.err.println("Server was shut down safely.");
    }
}
