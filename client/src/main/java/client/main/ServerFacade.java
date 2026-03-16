package client.main;

import com.google.gson.Gson;
import model.requests.*;
import model.resulsts.CreateGameResult;
import model.resulsts.ListGamesResult;
import model.resulsts.LoginResult;
import model.resulsts.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException{
        var request = buildRequest("POST", "/user", registerRequest , null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var request = buildRequest("POST", "/session", loginRequest , null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException {
        var request = buildRequest("DELETE", "/session", logoutRequest , logoutRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ResponseException {
        var request = buildRequest("GET", "/game", listGamesRequest , listGamesRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException {
        var request = buildRequest("POST", "/game", createGameRequest , createGameRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws ResponseException {
        var request = buildRequest("POST", "/game", joinGameRequest , joinGameRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String auth) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body))
                .setHeader("Accept", "application/json");
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (auth != null) {
            request.setHeader("authorization", auth);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ResponseException(status, ResponseException.fromJson(body));
            }

            throw new ResponseException(status, "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

