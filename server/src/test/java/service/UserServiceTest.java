package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import datamodel.*;
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
        assertFalse(authData.authToken().isEmpty());
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
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void badLogin() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        service.register(user);
        assertThrows(UnauthorizedException.class,
                () -> service.login(new LoginData("henry", "badPW"))
        );
    }

    @Test
    void logout() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        service.register(user);
        var authData = service.login(new LoginData("henry", "HEN123"));
        assertNotNull(db.getAuth(authData.authToken()));
        service.logout(authData.authToken());
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void badLogout() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        service.register(user);
        service.login(new LoginData("henry", "HEN123"));
        assertThrows(UnauthorizedException.class,
                () -> service.logout("badtoken")
        );
    }

    @Test
    void listGames() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        service.createGame(, new CreateRequest(authData.authToken(), "Game1"), );
        service.createGame(, new CreateRequest(authData.authToken(), "Game2"), );
        var games = service.listGames(authData.authToken());
        assertNotNull(games);
        assertFalse(games.isEmpty());
        assertEquals(db.listGames().size(), games.size());
        assertEquals(2, games.size());
    }

    @Test
    void listGamesBadAuth() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        assertThrows(UnauthorizedException.class,
                () -> service.listGames("bad_authToken")
        );
    }

    @Test
    void createGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var gameID = service.createGame(, new CreateRequest(authData.authToken(), "Game1"), );
        assertNotNull(gameID);
        assertNotNull(db.getGame(gameID));
        assertEquals(db.getGame(gameID).gameID(), gameID);
    }

    @Test
    void createBadGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        assertThrows(BadRequestException.class,
                () -> service.createGame(, new CreateRequest(authData.authToken(), null), )
        );
    }

    @Test
    void joinGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var gameID = service.createGame(, new CreateRequest(authData.authToken(), "Game1"), );
        service.joinGame(new JoinRequest(authData.authToken(), new JoinData("WHITE", gameID)));
        assertNotNull(db.getGame(gameID).whiteUsername());
        assertNull(db.getGame(gameID).blackUsername());

        var user3 = new UserData("henrychen", "henrychenshenry@henry.org", "HEN123");
        var authData3 = service.register(user3);
        service.joinGame(new JoinRequest(authData3.authToken(), new JoinData("BLACK", gameID)));
        assertNotNull(db.getGame(gameID).blackUsername());
    }

    @Test
    void joinTakenGame() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var gameID = service.createGame(, new CreateRequest(authData.authToken(), "Game1"), );
        service.joinGame(new JoinRequest(authData.authToken(), new JoinData("WHITE", gameID)));

        var user2 = new UserData("henrietta", "henriettashenry@henry.org", "HEN123");
        var authData2 = service.register(user2);
        assertThrows(AlreadyTakenException.class,
                () -> service.joinGame(new JoinRequest(authData2.authToken(), new JoinData("WHITE", gameID)))
        );
    }

    @Test
    void clear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var user2 = new UserData("henrietta", "henriettashenry@henry.org", "HEN123");
        service.register(user2);
        var gameID = service.createGame(, new CreateRequest(authData.authToken(), "Game1"), );
        assertNotNull(db.getGame(gameID));
        service.clear();
        assertNull(db.getUser(new LoginData(user.username(), user.password())));
        assertNull(db.getGame(gameID));
    }

    @Test
    void userAfterClear() throws Exception {
        DataAccess db = new MemoryDataAccess();
        UserService service = new UserService(db);
        var user = new UserData("henry", "henryshenry@henry.org", "HEN123");
        var authData = service.register(user);
        var user2 = new UserData("henrietta", "henriettashenry@henry.org", "HEN123");
        service.register(user2);
        service.clear();
        assertThrows(Exception.class,
                () -> service.createGame(, new CreateRequest(authData.authToken(), "Game1"), )
        );
    }
}