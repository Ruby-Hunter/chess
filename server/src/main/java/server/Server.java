package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
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
    }

    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            var authData = userServ.register(user);

            ctx.result(serializer.toJson(authData));
        } catch (Exception ex){
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            ctx.status(403).result(msg);
        }
    }

    private boolean login(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        var auth = serializer.fromJson(reqJson, AuthData.class);
        var loggedIn = userServ.login(auth);
        return loggedIn;
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
