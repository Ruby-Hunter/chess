package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import datamodel.*;

import java.util.HashSet;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;
    static int curID = 1;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
        curID = 1;
    }

    public AuthData register(UserData user) throws Exception{
        if(user.username() == null  ||  user.password() == null  ||  user.email() == null){ // Check to see if all fields are valid
            throw new BadRequestException("bad request");
        }
        if(dataAccess.getUser(new LoginData(user.username(), user.password())) != null){
            throw new AlreadyTakenException("already taken");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(LoginData login) throws Exception{
        if(login.username() == null  ||  login.password() == null){ // Check to see if all fields are valid
            throw new BadRequestException("bad request");
        }
        var userData = dataAccess.checkUser(login); // Finds UserData that matches login username
        if(userData == null){
            throw new UnauthorizedException("unauthorized");
        }
        var authData = new AuthData(login.username(), generateAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception{
        var authData = dataAccess.getAuth(authToken);
        if(authData == null){
            throw new UnauthorizedException("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    public HashSet<GameData> listGames(String authToken) throws Exception{
        var auth = dataAccess.getAuth(authToken);
        if(auth == null){
            throw new UnauthorizedException("unauthorized");
        }
        return dataAccess.listGames();
    }

    public Integer createGame(CreateRequest request) throws Exception {
        if(request.authToken() == null  ||  request.gameName() == null){
            throw new BadRequestException("bad request");
        }
        var auth = dataAccess.getAuth(request.authToken());
        if(auth == null){ // check if auth exists
            throw new UnauthorizedException("unauthorized");
        }
        int gameID = generateID();
        var game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        dataAccess.createGame(game);
        return gameID;
    }

    public void joinGame(JoinRequest request) throws Exception {
        if(request.authToken() == null  ||  request.joinData().gameID() == null  ||  request.joinData().playerColor() == null
                ||  !(request.joinData().playerColor().equals("WHITE")  ||  request.joinData().playerColor().equals("BLACK")) ){
            throw new BadRequestException("bad request");
        }
        var auth = dataAccess.getAuth(request.authToken());
        if(auth == null){ // check if auth exists
            throw new UnauthorizedException("unauthorized");
        }
        GameData gameData = dataAccess.getGame(request.joinData().gameID());
        if(gameData == null){
            throw new BadRequestException("bad request");
        }
        GameData newGame;
        if(request.joinData().playerColor().equals("WHITE")){
            if(gameData.whiteUsername() != null){
                throw new AlreadyTakenException("already taken");
            } else{
                newGame = new GameData(gameData.gameID(), auth.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
            }
        } else {
            if(gameData.blackUsername() != null){
                throw new AlreadyTakenException("already taken");
            } else{
                newGame = new GameData(gameData.gameID(), gameData.whiteUsername(), auth.username(), gameData.gameName(), gameData.game());
            }
        }
        dataAccess.updateGame(newGame);
    }

    public void clear() throws Exception{
        dataAccess.clear();
    }

    private String generateAuthToken(){
        return UUID.randomUUID().toString();
    }

    private Integer generateID(){
        return curID++;
    }
}
