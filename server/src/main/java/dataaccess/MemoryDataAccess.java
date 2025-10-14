package dataaccess;

import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>(); //Username, UserData
    private final HashMap<String, String> auths = new HashMap<>(); //authToken, username
    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public String getAuth(String authToken) {
        return auths.get(authToken);
    }
}
