package ui;

import com.google.gson.Gson;
import datamodel.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url){ serverURL = url; }

    public AuthData register(UserData user){
        var request = buildRequest
    }

    public AuthData login(LoginData login){}

    public void logout(String authToken){}

    public Integer createGame(String authToken, String gameName){}

    public HashSet<GameData> listGames(String authToken){}

    public void joinGame(String authToken, JoinData joinData){}

    public void clear(){}

    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if(body != null){
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object body){
        if(body != null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body));
        } else{
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest()
}
