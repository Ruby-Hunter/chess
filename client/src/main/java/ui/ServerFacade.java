package ui;

import com.google.gson.Gson;
import datamodel.*;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashSet;
import exception.ResponseException;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url){ serverURL = url; }

    public AuthData register(UserData user) throws Exception {
        var request = buildRequest("POST", "/user", user);
        var response = sendRequest(request);
        return
    }

    public AuthData login(LoginData login){return null;}

    public void logout(String authToken){}

    public Integer createGame(String authToken, String gameName){return null;}

    public HashSet<GameData> listGames(String authToken){return null;}

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

    private BodyPublisher makeRequestBody(Object body){
        if(body != null){
            return BodyPublishers.ofString(new Gson().toJson(body));
        } else{
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try{
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex){
            throw new Exception("response exception");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if(!isSuccessful(status)){
            var body = response.body();
            if(body != null){
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if(responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status){ return status / 100 == 2; }
}
