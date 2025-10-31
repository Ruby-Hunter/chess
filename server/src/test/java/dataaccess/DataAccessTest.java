package dataaccess;

import com.mysql.cj.log.Log;
import datamodel.*;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() {
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("ethan1", "berliner@donut.com", "passwort"));
        db.clear();
        assertNull(db.getUser(new LoginData("ethan1", "passwort")));
        db.createAuth(new AuthData("ethan1", "a2z"));
        db.clear();
        assertNull(db.getAuth("a2z"));
    }

    @Test
    void createUser() {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("ethan1", "berliner@donut.com", "passwort");
        db.createUser(user);
        assertEquals(user, db.getUser(new LoginData(user.username(), user.password())));
    }

    @Test
    void createAuth() {
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
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void listGames() {
    }
}