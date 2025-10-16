package dataaccess;

import datamodel.*;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>(); //username, UserData
    private final HashMap<String, AuthData> auths = new HashMap<>(); //authToken, AuthData
    private final HashMap<Integer, GameData> games = new HashMap<>(); //gameID, GameData

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
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
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public HashSet<GameData> listGames() {
        return (HashSet<GameData>) games.values();
    }

}
