package websocket.commands;

import chess.ChessMove;

public class UserMoveCommand extends UserGameCommand{
    ChessMove move;
    public UserMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }
}
