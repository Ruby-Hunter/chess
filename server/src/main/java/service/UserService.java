package service;

import dataaccess.DataAccess;
import datamodel.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception{
        if(dataAccess.getUser(user.username()) != null){
            throw new Exception("already exists");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public AuthData login(LoginData login) throws Exception{
        var userData = dataAccess.getUser(login.username()); // Finds UserData that matches login username
        if(userData == null){
            throw new Exception("unauthorized");
        }
        if(!Objects.equals(login.password(), userData.password())){ // Check if passwords match
            throw new Exception("unauthorized");
        }
        var authData = new AuthData(login.username(), generateAuthToken());
        dataAccess.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws Exception{
        var authData = dataAccess.getAuth(authToken);
        if(authData == null){
            throw new Exception("unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }

    private String generateAuthToken(){
        return UUID.randomUUID().toString();
    }
}
