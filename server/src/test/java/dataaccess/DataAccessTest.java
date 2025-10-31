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
        db.createUser(new UserData("ethan", "berliner@donut.com", "passwort"));
        db.clear();
        assertNull(db.getUser(new LoginData("ethan", "passwort")));
        db.createAuth(new AuthData("ethan", "a2z"));
        db.clear();
        assertNull(db.getAuth("a2z"));
    }

    @Test
    void createUser() {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("ethan", "berliner@donut.com", "passwort");
        db.createUser(user);
        assertEquals(user, db.getUser(new LoginData(user.username(), user.password())));
    }

    @Test
    void createAuth() {
    }

    @Test
    void getUser() {
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