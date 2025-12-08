package dataaccess;

import datamodel.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess{
    private final HashMap<String, UserData> users = new HashMap<>(); //username, UserData
    private final HashMap<String, AuthData> auths = new HashMap<>(); //authToken, AuthData
    private final HashMap<Integer, GameData> games = new HashMap<>(); //gameID, GameData
    private int id = 0;

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
    public int createGame(GameData game) {
        int gameID = generateGameID();
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public UserData getUser(LoginData login) {
        return users.get(login.username());
    }

    @Override
    public UserData checkUser(LoginData login) {
        var user = users.get(login.username());
        if((user != null) && (Objects.equals(user.password(), login.password()))){
            return user;
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public boolean checkAuth(String username) throws Exception {
        return false;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return games.get(gameID);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public HashSet<GameData> listGames() {
        return new HashSet<>(games.values());
    }

    @Override
    public void updateGame(GameData updatedGame) {
        games.put(updatedGame.gameID(), updatedGame);
    }

    private int generateGameID(){
        return id++;
    }
}
