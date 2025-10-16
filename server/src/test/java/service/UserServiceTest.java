package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() throws Exception{
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }
}