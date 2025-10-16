package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userServ;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userServ = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", ctx -> register(ctx));
        server.post("session", ctx -> login(ctx));
        server.delete("session", ctx -> logout(ctx));
        server.get("game", ctx -> listGames(ctx));
        server.post("game", ctx -> createGame(ctx));
        server.put("game", ctx -> joinGame(ctx));
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
            String reqJson = ctx.header("authorization");
            var auth = serializer.fromJson(reqJson, String.class);
            userServ.logout(auth);
            ctx.result(serializer.toJson(""));
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
            String reqJson = ctx.header("authorization");
            String auth = serializer.fromJson(reqJson, String.class);
            var gamesList = userServ.listGames(auth);
            ctx.result(serializer.toJson(gamesList));
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
            String reqJsonHeader = ctx.header("authorization");
            String reqJsonBody = ctx.body();
            String auth = serializer.fromJson(reqJsonHeader, String.class);
            String gameName = serializer.fromJson(reqJsonBody, String.class);
            Integer gameID = userServ.createGame(auth, gameName);
            ctx.result(serializer.toJson(gameID));
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
            String reqJsonHeader = ctx.header("authorization");
            String reqJsonBody = ctx.body();
            String auth = serializer.fromJson(reqJsonHeader, String.class);
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
