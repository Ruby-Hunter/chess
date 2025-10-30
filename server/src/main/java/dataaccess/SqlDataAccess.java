package dataaccess;

import datamodel.*;
import org.mindrot.jbcrypt.BCrypt;


import java.sql.SQLException;
import java.util.HashSet;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              'id' int NOT NULL AUTOINCREMENT,
              'username' varchar(128) NOT NULL,
              'password' TEXT NOT NULL,
              'email' TEXT NOT NULL,
              PRIMARY KEY ('id'),
              INDEX(username)
            """,

            """
            CREATE TABLE IF NOT EXISTS auths (
              'id' int NOT NULL,
              'username' varchar(128) NOT NULL,
              'authToken' TEXT NOT NULL,
              PRIMARY KEY ('id'),
              INDEX (authToken)
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              'gameID' int NOT NULL,
              'whiteUsername' TEXT,
              'blackUsername' TEXT,
              'gameName' varchar(128) NOT NULL,
              'authToken' TEXT NOT NULL,
              PRIMARY KEY ('gameID')
            """
    };

    void storeUserPassword(String username, String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        writeHashedPasswordToDatabase(username, hashedPassword);
    }

    boolean verifyUser(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    void writeHashedPasswordToDatabase(String username, String hashedPassword){

    }

    String readHashedPasswordFromDatabase(String username){
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {
        try(var conn = DatabaseManager.getConnection()){
            var stat = conn.prepareStatement("");
            stat.setString(1, user.username());
            stat.executeUpdate();
        } catch(Exception ex){

        }
    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public UserData getUser(String username) {
        executeQuery()
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
