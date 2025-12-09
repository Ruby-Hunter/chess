package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import datamodel.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
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
//        if(dataAccess.checkAuth(login.username())){
//            throw new UnauthorizedException("User already logged in");
//        }
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

    public Integer createGame(String authToken, String gameName) throws Exception {
        if(authToken == null  ||  gameName == null){
            throw new BadRequestException("bad request");
        }
        var auth = dataAccess.getAuth(authToken);
        if(auth == null){ // check if auth exists
            throw new UnauthorizedException("unauthorized");
        }
        var game = new GameData(null, null, null, gameName, new ChessGame());
        return dataAccess.createGame(game);
    }

    public void joinGame(JoinRequest request) throws Exception {
        if(request.authToken() == null  ||  request.joinData().gameID() == null  ||  request.joinData().playerColor() == null
                ||  !(request.joinData().playerColor().equalsIgnoreCase("WHITE") || request.joinData().playerColor().equalsIgnoreCase("BLACK")) ){
            throw new BadRequestException("bad request");
        }
        var auth = dataAccess.getAuth(request.authToken());
        if(auth == null){ // check if auth exists
            throw new UnauthorizedException("unauthorized");
        }
        String username = auth.username();
        GameData gameData = dataAccess.getGame(request.joinData().gameID());
        if(gameData == null){
            throw new BadRequestException("bad request");
        }
//        if(Objects.equals(gameData.whiteUsername(), username) || Objects.equals(gameData.blackUsername(), username)){
//            throw new AlreadyTakenException("Player is already in game");
//        }
        GameData newGame;
        if(request.joinData().playerColor().equalsIgnoreCase("WHITE")){
            if(gameData.whiteUsername() != null){
                throw new AlreadyTakenException("already taken");
            } else{
                newGame = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            }
        } else {
            if(gameData.blackUsername() != null){
                throw new AlreadyTakenException("already taken");
            } else{
                newGame = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
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
}
