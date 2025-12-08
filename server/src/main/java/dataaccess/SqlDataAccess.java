package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import datamodel.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() {
        try {
            configureDatabase();
        } catch (Exception ex){
            System.err.println("Database Creation error");
        }
    }

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            for(String statement : createStatements){
                try(var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
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

            "DROP TABLE IF EXISTS auths;",

            """
            CREATE TABLE IF NOT EXISTS auths (
              username varchar(128) NOT NULL,
              authToken varchar(256) NOT NULL,
              PRIMARY KEY (authToken),
              INDEX(username)
            );
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              gameID int NOT NULL AUTO_INCREMENT,
              whiteUsername TEXT,
              blackUsername TEXT,
              gameName varchar(128) NOT NULL,
              game TEXT NOT NULL,
              PRIMARY KEY (gameID)
            );
            """
    };

    private String encryptPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    private boolean verifyUser(String providedClearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    @Override
    public void clear() throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.createStatement()){
                statement.executeUpdate("TRUNCATE TABLE users;");
                statement.executeUpdate("TRUNCATE TABLE games;");
                statement.executeUpdate("TRUNCATE TABLE auths;");
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO users VALUES (?, ?, ?);");
            statement.setString(1, user.username());
            statement.setString(2, user.email());
            statement.setString(3, encryptPassword(user.password()));
            statement.executeUpdate();
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData auth) throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("INSERT INTO auths VALUES (?, ?);");
            statement.setString(1, auth.username());
            statement.setString(2, auth.authToken());
            statement.executeUpdate();
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    /* Creates the game and returns the gameID */
    @Override
    public int createGame(GameData gameData) throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement(
                    "INSERT INTO games VALUES (?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setNull(1, Types.INTEGER);
            statement.setNull(2, Types.LONGNVARCHAR);
            statement.setNull(3, Types.LONGNVARCHAR);
            statement.setString(4, gameData.gameName());
            statement.setString(5, new Gson().toJson(gameData.game()));
            statement.executeUpdate();

            try(ResultSet keys = statement.getGeneratedKeys()){ // get the gameID
                if(keys.next()){
                    return keys.getInt(1);
                }
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return 0;
    }

    @Override
    public UserData getUser(LoginData login) throws Exception { // returns UserData if username matches, else returns null
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, email, password FROM users WHERE username = ?;");
            statement.setString(1, login.username());
            try(ResultSet rs = statement.executeQuery()){
                if(!rs.next()){
                    return null;
                }
                String email = rs.getString("email");
                return new UserData(login.username(), email, login.password());
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    public boolean checkAuth(String username) throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username FROM auths WHERE username = ?;");
            statement.setString(1, username);
            try(ResultSet rs = statement.executeQuery()){
                return rs.next(); // returns true if already logged in
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public UserData checkUser(LoginData login) throws Exception { // returns UserData if login matches it, else returns nulll
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, email, password FROM users WHERE username = ?;");
            statement.setString(1, login.username());
            try(ResultSet rs = statement.executeQuery()){
                if(!rs.next()){
                    return null;
                }
                String email = rs.getString("email");
                String hashedPassword = rs.getString("password");
                if(verifyUser(login.password(), hashedPassword)){
                    return new UserData(login.username(), email, login.password());
                }
                return null;
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT username, authToken FROM auths WHERE authToken = ?;");
            statement.setString(1, authToken);
            try(ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String userName = rs.getString("username");
                return new AuthData(userName, authToken);
            }
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws Exception {
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?;");
            statement.setInt(1, gameID);
            try(ResultSet rs = statement.executeQuery()) {
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
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws Exception { // Drop row from table
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("DELETE FROM auths where authToken = ?;");
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public HashSet<GameData> listGames() throws Exception {
        HashSet<GameData> gameList = new HashSet<>();
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games;");
            try(ResultSet rs = statement.executeQuery()) {
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
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        return gameList;
    }

    @Override
    public void updateGame(GameData updatedGame) throws Exception { // Changes usernames and/or game
        try(var conn = DatabaseManager.getConnection()){
            var statement = conn.prepareStatement("UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?;");
            statement.setString(1, updatedGame.whiteUsername());
            statement.setString(2, updatedGame.blackUsername());
            statement.setString(3, new Gson().toJson(updatedGame.game()));
            statement.setInt(4, updatedGame.gameID());
            statement.executeUpdate();
        } catch(SQLException ex){
            throw new SQLException(ex.getMessage());
        } catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
    }
}
