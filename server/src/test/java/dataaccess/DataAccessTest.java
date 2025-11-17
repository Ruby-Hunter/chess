package dataaccess;

import org.junit.jupiter.api.Test;
import datamodel.*;
import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("ethan1", "berliner@donut.com", "passwort"));
        db.clear();
        assertNull(db.getUser(new LoginData("ethan1", "passwort")));
        db.createAuth(new AuthData("ethan1", "a2z"));
        db.clear();
        assertNull(db.getAuth("a2z"));
    }

    @Test
    void createUser() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("ethan1", "berliner@donut.com", "passwort");
        db.createUser(user);
        assertEquals(user, db.getUser(new LoginData(user.username(), user.password())));
    }

    @Test
    void createAuth() throws Exception {
        DataAccess db = new MemoryDataAccess();
        String authToken = "token";
        var auth = new AuthData("ethan", authToken);
        db.createAuth(auth);
        assertEquals(auth, db.getAuth(authToken));
    }

    @Test
    void getUser() {
    }

    @Test
    void checkUser(){
    }

    @Test
    void getAuth() throws Exception {
        createAuth();
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void listGames() {
    }
}