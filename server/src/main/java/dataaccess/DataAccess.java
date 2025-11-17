package dataaccess;

import datamodel.*;

import java.util.HashSet;

public interface DataAccess {
    void clear() throws Exception;
    void createUser(UserData user) throws Exception;
    void createAuth(AuthData auth) throws Exception;
    void createGame(GameData game) throws Exception;
    UserData getUser(LoginData login) throws Exception;
    UserData checkUser(LoginData login) throws Exception;
    AuthData getAuth(String authToken) throws Exception;
    GameData getGame(Integer gameID) throws Exception;
    void deleteAuth(String authToken) throws Exception;
    HashSet<GameData> listGames() throws Exception;
    void updateGame(GameData updatedGame) throws Exception;
}
