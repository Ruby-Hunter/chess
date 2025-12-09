package websocket.messages;

import chess.ChessGame;

public class ServerLoadGameMessage extends ServerMessage{
    private ChessGame game;
    private ChessGame.TeamColor color;

    public ServerLoadGameMessage(String message, ChessGame game, ChessGame.TeamColor color) {
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
