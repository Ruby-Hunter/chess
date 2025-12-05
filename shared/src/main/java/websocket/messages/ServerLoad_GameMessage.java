package websocket.messages;

import chess.ChessGame;

public class ServerLoad_GameMessage extends ServerMessage{
    private ChessGame game;

    public ServerLoad_GameMessage(String message, ChessGame game) {
        super(ServerMessageType.LOAD_GAME, "");
        this.game = game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame(){
        return game;
    }
}
