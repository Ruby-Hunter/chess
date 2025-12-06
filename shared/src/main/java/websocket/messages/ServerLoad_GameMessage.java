package websocket.messages;

import chess.ChessGame;

public class ServerLoad_GameMessage extends ServerMessage{
    private ChessGame game;
    private ChessGame.TeamColor color;

    public ServerLoad_GameMessage(String message, ChessGame game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME, message);
        this.game = game;
        this.color = color;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame(){
        return game;
    }

    public ChessGame.TeamColor getColor(){
        return color;
    }
}
