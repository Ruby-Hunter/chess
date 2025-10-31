package dataaccess;

import datamodel.AuthData;
import datamodel.LoginData;
import datamodel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SQLDataAccessTest {

    @Test
    void constructorTest() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
    }

    @Test
    void clear() throws Exception {
        DataAccess db = new SqlDataAccess();
        db.createUser(new UserData("ethan", "berliner@donut.com", "passwort"));
        db.clear();
        assertNull(db.getUser(new LoginData("ethan", "passwort")));
        db.createAuth(new AuthData("ethan", "a2z"));
        db.clear();
        assertNull(db.getAuth("a2z"));
    }

    @Test
    void createUser() throws Exception {
        DataAccess db = new SqlDataAccess();
        var user = new UserData("ethan", "berliner@donut.com", "passwort");
        db.createUser(user);
        assertEquals(user, db.getUser(new LoginData(user.username(), user.password())));
    }

    @Test
    void createAuth() {
        DataAccess db = new SqlDataAccess();
    }

    @Test
    void getUser() throws Exception {
        DataAccess db = new SqlDataAccess();
        var user = new UserData("ethan", "berliner@donut.com", "passwort");
        db.createUser(user);
        var login = new LoginData("ethanFalsch", "falschesPasswort");
        UserData got = db.getUser(login);
        Assertions.assertNotNull(got);
    }

    @Test
    void getFakeUser() throws Exception {
        DataAccess db = new SqlDataAccess();
        var login = new LoginData("ethanFalsch", "falschesPasswort");
        UserData got = db.getUser(login);
        assertNull(got);
    }

    @Test
    void checkUser() {
    }

    @Test
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void listGames() {
    }
}