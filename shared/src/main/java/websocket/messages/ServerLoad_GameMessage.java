package websocket.messages;

import chess.ChessGame;

public class ServerLoad_GameMessage extends ServerMessage{
    private ChessGame game;

    public ServerLoad_GameMessage(ServerMessageType type, String message, ChessGame game) {
        super(type, "");
        this.game = game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    ChessGame getGame(){
        return game;
    }
}
