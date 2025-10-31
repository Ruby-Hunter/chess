package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.*;
import org.mindrot.jbcrypt.BCrypt;
import service.*;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() {
        try {
            configureDatabase();
        } catch (DataAccessException ex){
            System.err.println("Database Creation error");
        }
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
            ex.printStackTrace();
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              username varchar(128) NOT NULL,
              email varchar(128) NOT NULL,
              password TEXT NOT NULL,
              PRIMARY KEY (username)
            );
            """,

            """
            CREATE TABLE IF NOT EXISTS auths (
              username varchar(128) NOT NULL,
              authToken varchar(256) NOT NULL,
              PRIMARY KEY (username),
              INDEX(authToken)
            );
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              gameID int NOT NULL,
              whiteUsername TEXT,
              blackUsername TEXT,
              gameName varchar(128) NOT NULL,
              game TEXT NOT NULL,
              PRIMARY KEY (gameID)
            );
            """
    };

    String encryptPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    boolean verifyUser(String providedClearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.createStatement()){
                statement.executeUpdate("TRUNCATE TABLE users;");
                statement.executeUpdate("TRUNCATE TABLE games;");
                statement.executeUpdate("TRUNCATE TABLE auths;");
            }
        } catch (SQLException ex){
            System.err.println("SQL clear problem");
        } catch(Exception ex){
            System.err.println("clear problem");
        }
    }

    @Override
    public void createUser(UserData user) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?);");
            statement.setString(1, user.username());
            statement.setString(2, user.email());
            statement.setString(3, encryptPassword(user.password()));
            statement.executeUpdate();
        } catch (SQLException ex){
            System.err.println("SQL createUser problem");
        } catch(Exception ex){
            System.err.println("createUser problem");
        }
    }

    @Override
    public void createAuth(AuthData auth) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO auths VALUES (?, ?);");
            statement.setString(1, auth.username());
            statement.setString(2, auth.authToken());
            statement.executeUpdate();
        } catch (SQLException ex){
            System.err.println("SQL AuthData problem");
        } catch(Exception ex){
            System.err.println("createAuth problem");
        }
    }

    @Override
    public void createGame(GameData gameData) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO games VALUES (?, ?, ?, ?, ?);");
            statement.setInt(1, gameData.gameID());
            statement.setNull(2, Types.LONGNVARCHAR);
            statement.setNull(3, Types.LONGNVARCHAR);
            statement.setString(4, gameData.gameName());
            statement.setString(5, new Gson().toJson(gameData.game()));
            statement.executeUpdate();
        } catch (SQLException ex){
            System.err.println("SQL createGame problem");
        } catch(Exception ex){
            System.err.println("createAuth problem");
        }
    }

    @Override
    public UserData getUser(LoginData login) { // returns UserData if username matches, else returns null
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, email, password FROM users WHERE username = ?;");
            statement.setString(1, login.username());
            try(var rs = statement.executeQuery()){
                if(!rs.next()){
                    return null;
                }
                String email = rs.getString("email");
                return new UserData(login.username(), email, login.password());
            }
        } catch (SQLException ex){
            System.err.println("SQL getUser problem");
        } catch(Exception ex){
            System.err.println("getUser problem");
        }
        return null;
    }

    @Override
    public UserData checkUser(LoginData login) { // returns UserData if login matches it, else returns nulll
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, email, password FROM users WHERE username = ?;");
            statement.setString(1, login.username());
            try(var rs = statement.executeQuery()){
                if(!rs.next()){
                    return null;
                }
                String email = rs.getString("email");
                String hashedPassword = rs.getString("password");
                if(verifyUser(login.password(), hashedPassword)){
                    return new UserData(login.username(), email, login.password());
                }
            }
        } catch (SQLException ex){
            System.err.println("SQL getUser problem");
        } catch(Exception ex){
            System.err.println("getUser problem");
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, authToken FROM auths WHERE authToken = ?;");
            statement.setString(1, authToken);
            try(var rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String userName = rs.getString("username");
                return new AuthData(userName, authToken);
            }
        } catch (SQLException ex){
            System.err.println("SQL getAuth problem");
        } catch(Exception ex){
            System.err.println("getAuth problem");
        }
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?;");
            statement.setInt(1, gameID);
            try(var rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String gameJson = rs.getString("game");
                ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            }
        } catch(Exception ex){
            System.err.println("getGame problem");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) { // Drop row from table
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("DELETE FROM auths where authToken = ?;");
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch(Exception ex){
            System.err.println("deleteAuth problem");
        }
    }

    @Override
    public HashSet<GameData> listGames() {
        HashSet<GameData> gameList = new HashSet<>();
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games;");
            try(var rs = statement.executeQuery()) {
                while (rs.next()) {
                    int gameID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    String gameString = rs.getString("game");
                    ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                    gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
            }
        } catch (SQLException ex){
            System.err.println("SQL getAuth problem");
        } catch(Exception ex){
            System.err.println("getAuth problem");
        }
        return gameList;
    }

    @Override
    public void updateGame(GameData updatedGame) {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?;");
            statement.setString(1, updatedGame.whiteUsername());
            statement.setString(2, updatedGame.blackUsername());
            statement.setString(3, new Gson().toJson(updatedGame.blackUsername()));
            statement.setInt(4, updatedGame.gameID());
            statement.executeUpdate();
        } catch (SQLException ex){
            System.err.println("SQL getAuth problem");
        } catch(Exception ex){
            System.err.println("getAuth problem");
        }
    }
}
