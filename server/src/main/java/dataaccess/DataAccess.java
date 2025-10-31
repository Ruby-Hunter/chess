package dataaccess;

import com.mysql.cj.log.Log;
import datamodel.*;

import java.util.HashSet;
import java.util.Set;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    void createAuth(AuthData auth);
    void createGame(GameData game);
    UserData getUser(LoginData login);
    AuthData getAuth(String authToken);
    GameData getGame(Integer gameID);
    void deleteAuth(String authToken);
    HashSet<GameData> listGames();
    void updateGame(GameData updatedGame);
}
