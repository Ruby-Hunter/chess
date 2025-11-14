package ui;

import java.net.http.HttpClient;
import datamodel.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url){ serverURL = url; }

    public AuthData register(RegisterRequest request){}

    public LoginResult login(LoginRequest request){}

    public JoinResult joinGame(JoinRequest request){}
}
