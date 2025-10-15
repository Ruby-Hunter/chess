package service;

import dataaccess.DataAccess;
import datamodel.*;

import java.util.Objects;

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

        return authData;
    }

    public AuthData login(LoginData login) throws Exception{
        var userData = dataAccess.getUser(login.username());
        if(userData == null){
            throw new Exception("unauthorized");
        }

        if(Objects.equals(login.password(), userData.password())){
            var authData = new AuthData(user.username(), generateAuthToken());
            return authData;
        }

    }

    private String generateAuthToken(){
        return "hallo";
    }
}
