package ui;

import com.google.gson.Gson;
import datamodel.*;
import exception.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

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

    public Integer createGame(String authToken, String gameName) throws ResponseException {
        var request = buildRequest("POST", "/game", new CreateRequest(gameName), authToken);
        var response = sendRequest(request);
        CreateGameResponse resp = handleResponse(response, CreateGameResponse.class);
        return resp.gameID();
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResponse.class);
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
                .uri(URI.create(serverURL + path));
        if(body != null){
            request.setHeader("Content-Type", "application/json");
            request.method(method, makeRequestBody(body));
        }else{
            request.method(method, BodyPublishers.noBody());
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
            if(status == 400){
                throw new BadRequestException("");
            }
            if(status == 401){
                throw new UnauthorizedException("");
            }
            if(status == 403){
                throw new AlreadyTakenException("");
            }
            if(status == 500){
                System.err.println("Unknown error");
            }
            var body = response.body();
            if(body != null){
                throw new ResponseException(ResponseException.fromHttpStatusCode(status), "");
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "");
        }

        if(responseClass != null){
            try{
                return new Gson().fromJson(response.body(), responseClass);
            } catch (Exception ex){
                throw new ResponseException(ResponseException.Code.ServerError,
                        "Invalid server response" + response.body());
            }
        }

        return null;
    }

    private boolean isSuccessful(int status){ return status / 100 == 2; }
}
