package dataaccess;

import datamodel.*;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>(); //username, UserData
    private final HashMap<String, AuthData> auths = new HashMap<>(); //authToken, AuthData

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }
}
