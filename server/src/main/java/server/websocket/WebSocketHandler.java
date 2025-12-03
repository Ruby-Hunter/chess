package server.websocket;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.websocket.WsMessageContext;

public class WebSocketHandler {
//    public static void main(String[] args) {
//        Javalin.create()
//                .get("/echo/{msg}", ctx -> ctx.result("HTTP response: " + ctx.pathParam("msg")))
//                .ws("/ws", ws -> {
//                    ws.onConnect(ctx -> {
//                        ctx.enableAutomaticPings();
//                        System.out.println("Websocket connected");
//                    });
//                    ws.onMessage(ctx -> ctx.send("WebSocket response:" + ctx.message()));
//                    ws.onClose(_ -> System.out.println("Websocket closed"));
//                })
//                .start(8080);
//    }

    public WebSocketHandler(){
    }

    public void echo(WsMessageContext ctx){
        ctx.send("WebSocket response:" + ctx.message());
    }

    public void closeMessage(){
        System.err.println("Websocket closed");
    }
}
