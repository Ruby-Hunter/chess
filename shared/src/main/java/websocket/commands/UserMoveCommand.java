package websocket.commands;

public class UserMoveCommand extends UserGameCommand{
    public UserMoveCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
