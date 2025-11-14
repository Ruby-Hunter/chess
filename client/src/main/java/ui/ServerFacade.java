package ui;

import com.google.gson.Gson;
import datamodel.*;
import exception.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashSet;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverURL;

    public ServerFacade(String url){ serverURL = url; }

    public AuthData register(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginData login) throws ResponseException {
        var request = buildRequest("POST", "/session", login, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public Integer createGame(CreateRequest createRequest) throws ResponseException {
        var request = buildRequest("POST", "/game", createRequest.gameName(), createRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, Integer.class);
    }

    public HashSet listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, HashSet.class);
    }

    public void joinGame(JoinRequest joinRequest) throws ResponseException {
        var request = buildRequest("PUT", "/game", joinRequest.joinData(), joinRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path))
                .method(method, makeRequestBody(body));
        if(body != null){
            request.setHeader("Content-Type", "application/json");
        }
        if(authToken != null){
            request.setHeader("authorization", authToken);
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try{
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
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
