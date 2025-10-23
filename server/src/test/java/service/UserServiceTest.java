package service;

import dataaccess.*;
import datamodel.*;
import service.*;
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
    void login() throws Exception{
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        service.register(user);
        var authData = service.login(new LoginData("henry", "HEN123"));
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
    }

    @Test
    void logout() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        service.register(user);
        var authData = service.login(new LoginData("henry", "HEN123"));
        service.logout(authData.authToken());
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void listGames() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var gameID = service.createGame(authData.authToken(), "Game1");
        var gameID2 = service.createGame(authData.authToken(), "Game2");
        var games = service.listGames(authData.authToken());
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(db.listGames().size(), games.size());
        assertEquals(db.listGames().size(), 2);
    }

    @Test
    void createGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var gameID = service.createGame(authData.authToken(), "Game1");
        assertNotNull(gameID);
        assertNotNull(db.getGame(gameID));
    }

    @Test
    void joinGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var user2 = new UserData("henrietta", "henriettashenry@henry.org", "HEN123");
        var authData2 = service.register(user2);
        var gameID = service.createGame(authData.authToken(), "Game1");
        service.joinGame(authData.authToken(), new JoinData("WHITE", gameID));
        assertNotNull(db.getGame(gameID).whiteUsername());
        assertNull(db.getGame(gameID).blackUsername());

        Exception exception = assertThrows(AlreadyTakenException.class,
                () -> service.joinGame(authData2.authToken(), new JoinData("WHITE", gameID))
        );

        var user3 = new UserData("henrychen", "henrychenshenry@henry.org", "HEN123");
        var authData3 = service.register(user3);
        service.joinGame(authData3.authToken(), new JoinData("BLACK", gameID));
        assertNotNull(db.getGame(gameID).blackUsername());
    }

    @Test
    void clear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var user2 = new UserData("henrietta", "henriettashenry@henry.org", "HEN123");
        service.register(user2);
        var gameID = service.createGame(authData.authToken(), "Game1");
        assertNotNull(db.getGame(gameID));
        service.clear();
        assertNull(db.getUser(user.username()));
        assertNull(db.getGame(gameID));
    }
}