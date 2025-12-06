package websocket.commands;

import chess.ChessGame;

public class UserConnectCommand extends UserGameCommand{
    ChessGame.TeamColor color;
    public UserConnectCommand(String authToken, Integer gameID, ChessGame.TeamColor color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public ChessGame.TeamColor getColor(){
        return color;
    }
}
