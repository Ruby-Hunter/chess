package dataaccess;

import datamodel.*;


import java.util.HashSet;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException{
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public HashSet<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame(GameData updatedGame) {

    }
}
