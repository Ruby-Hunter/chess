package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SQLDataAccessTest {

    @Test
    void constructorTest() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
    }

    @Test
    void clear() throws DataAccessException {
        DataAccess db = new SqlDataAccess();
        db.createUser(new UserData("ethan", "berliner@donut.com", "passwort"));
        db.clear();
        assertNull(db.getUser("ethan"));
        db.createAuth(new AuthData("ethan", "a2z"));
        db.clear();
        assertNull(db.getAuth("a2z"));
    }

    @Test
    void createUser() {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("ethan", "berliner@donut.com", "passwort");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
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