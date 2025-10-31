package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.mysql.cj.log.Log;
import datamodel.*;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.UnauthorizedException;


import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            for(String statement : createStatements){
                try(var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch(DataAccessException | SQLException ex){
            System.err.print("Database configure error");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              'username' varchar(128) NOT NULL,
              'email' TEXT NOT NULL,
              'password' TEXT NOT NULL,
              PRIMARY KEY ('username')
            """,

            """
            CREATE TABLE IF NOT EXISTS auths (
              'username' varchar(128) NOT NULL,
              'authToken' TEXT NOT NULL,
              PRIMARY KEY ('username'),
              INDEX (authToken)
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              'gameID' int NOT NULL,
              'whiteUsername' TEXT,
              'blackUsername' TEXT,
              'gameName' varchar(128) NOT NULL,
              'game' TEXT NOT NULL,
              PRIMARY KEY ('gameID')
            """
    };

    String encryptPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    boolean verifyUser(String username, String providedClearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    String readHashedPasswordFromDatabase(String username){
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
            statement.setString(1, username);
            var rs = statement.executeQuery();
            rs.next();
            return rs.getString(0);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?)");
            statement.setString(1, user.username());
            statement.setString(2, user.email());
            statement.setString(3, encryptPassword(user.password()));
            statement.executeUpdate();
        } catch(Exception ex){
            System.err.println("createUser problem");
        }
    }

    @Override
    public void createAuth(AuthData auth) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO auths VALUES (?, ?)");
            statement.setString(1, auth.username());
            statement.setString(2, auth.authToken());
            statement.executeUpdate();
        } catch(Exception ex){
            System.err.println("createAuth problem");
        }
    }

    @Override
    public void createGame(GameData gameData) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO games VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, gameData.gameID());
            statement.setNull(2, Types.LONGNVARCHAR);
            statement.setNull(3, Types.LONGNVARCHAR);
            statement.setString(4, gameData.gameName());
            statement.setString(5, new Gson().toJson(gameData.game()));
            statement.executeUpdate();
        } catch(Exception ex){
            System.err.println("createAuth problem");
        }
    }

    @Override
    public UserData getUser(LoginData login) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, email, password FROM users WHERE username = ?");
            statement.setString(1, login.username());
            var rs = statement.executeQuery();
            if(!rs.next()){
                return null;
            }
            String email = rs.getString("email");
            String hashedPassword = rs.getString("password");
            if(!verifyUser(login.username(), login.password(), hashedPassword)){
                return new UserData(login.username(), email, login.password());
            }
            else{
                throw new UnauthorizedException("unauthorized");
            }
        } catch(Exception ex){
            System.err.println("getUser problem");
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, authToken FROM auths WHERE authToken = ?");
            statement.setString(1, authToken);
            var rs = statement.executeQuery();
            if(!rs.next()){
                return null;
            }
            String userName = rs.getString("username");
            return new AuthData(userName, authToken);
        } catch(Exception ex){
            System.err.println("getAuth problem");
        }
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?");
            statement.setInt(1, gameID);
            var rs = statement.executeQuery();
            if(!rs.next()){
                return null;
            }
            String whiteUsername = rs.getString("whiteUsername");
            String blackUsername = rs.getString("blackUsername");
            String gameName = rs.getString("gameName");
            String gameJson = rs.getString("game");
            ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        } catch(Exception ex){
            System.err.println("getGame problem");
        }
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
