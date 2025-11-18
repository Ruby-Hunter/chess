package client;

import datamodel.*;
import exception.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.Client;
import ui.ServerFacade;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade fac;
    String username1 = "user"; String username2 = "ethan";
    String email1 = "mail"; String email2 = "david@davidsdavid.com";
    String password1 = "pass"; String password2 = "goodpassword";
    UserData user1 = new UserData(username1, email1, password1);
    UserData user2 = new UserData(username2, email2, password2);
    LoginData login1 = new LoginData(username1, password1);
    LoginData login2 = new LoginData(username2, password2);
    LoginData badLogin = new LoginData(username1, password2);

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        fac = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void goodRegister() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        assertEquals(auth.username(), username1);
    }

    @Test
    void badRegister() throws ResponseException {
        fac.clear();
        fac.register(user1);
        assertThrows(AlreadyTakenException.class, () -> fac.register(user1));
    }

    @Test
    void goodLogin() throws ResponseException {
        fac.clear();
        fac.register(user1);
        AuthData auth = fac.login(login1);
        assertEquals(auth.username(), username1);
    }

    @Test
    void badLogin() throws ResponseException {
        fac.clear();
        fac.register(user1);
        assertThrows(UnauthorizedException.class, () -> fac.login(login2));
    }

    @Test
    void goodLogout() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        assertDoesNotThrow(() -> fac.logout(auth.authToken()));
    }

    @Test
    void badLogout() throws ResponseException {
        fac.clear();
        assertThrows(UnauthorizedException.class, () -> fac.logout(password2));
    }

    @Test
    void createGame() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        assertDoesNotThrow(() -> fac.createGame(auth.authToken(), "game1"));
    }

    @Test
    void createGameBadAuth() throws ResponseException {
        fac.clear();
        assertThrows(UnauthorizedException.class, () -> fac.createGame("badauth", "game"));
    }

    @Test
    void listGames() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        fac.createGame(auth.authToken(), "game1");
        fac.createGame(auth.authToken(), "game2");
        ListGamesResponse list = fac.listGames(auth.authToken());
        assertEquals(2, list.games().size());
    }

    @Test
    void listGamesBadAuth() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        fac.createGame(auth.authToken(), "game1");
        assertThrows(UnauthorizedException.class, () -> fac.createGame("auth", "game2"));
    }

    @Test
    void joinGame() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        int gameID = fac.createGame(auth.authToken(), "game1");
        fac.joinGame(new JoinRequest(auth.authToken(), new JoinData("WHITE", gameID)));
    }

    @Test
    void joinGameAlreadyTaken() throws ResponseException {
        fac.clear();
        AuthData auth = fac.register(user1);
        AuthData auth2 = fac.register(user2);
        int gameID = fac.createGame(auth.authToken(), "game1");
        fac.joinGame(new JoinRequest(auth.authToken(), new JoinData("WHITE", gameID)));
        assertThrows(AlreadyTakenException.class, () ->
                fac.joinGame(new JoinRequest(auth2.authToken(), new JoinData("WHITE", gameID))));
    }

    @Test
    void clear() throws ResponseException {
        fac.clear();
        fac.register(user1);
        fac.clear();
        assertDoesNotThrow(() -> fac.register(user1));
    }

    @Test
    void alreadyClear() throws ResponseException {
        AuthData auth = fac.register(user2);
        fac.createGame(auth.authToken(), "game2");
        fac.clear();
        assertThrows(UnauthorizedException.class, () -> fac.logout(auth.authToken()));
    }
}
