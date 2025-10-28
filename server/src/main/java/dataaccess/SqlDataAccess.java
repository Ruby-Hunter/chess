package dataaccess;

import datamodel.*;


import java.sql.SQLException;
import java.util.HashSet;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException{
        DatabaseManager.createDatabase();
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            try(var preparedStatement = conn.prepareStatement("SELECT 1+1")){
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        }
        catch(DataAccessException | SQLException ex){

        }
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
