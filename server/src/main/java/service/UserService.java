package service;

import dataaccess.DataAccess;
import datamodel.*;

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
        if(dataAccess.getUser(user.username()) != null){
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
        var userData = dataAccess.getUser(login.username()); // Finds UserData that matches login username
        if(userData == null){
            throw new UnauthorizedException("unauthorized");
        }
        if(!Objects.equals(login.password(), userData.password())){ // Check if passwords match
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

    public Integer createGame(String authToken, String gameName){

    }

    private String generateAuthToken(){
        return UUID.randomUUID().toString();
    }
}
