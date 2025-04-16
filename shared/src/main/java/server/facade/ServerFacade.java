package server.facade;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import server.request.*;
import server.result.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) throws ResponseException {
        serverUrl = url;
    }



    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, new ClearRequest(), ClearResult.class);
    }


    //user routes
    public RegisterResult register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", path, null, request, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws ResponseException {
        var path = "/session";
        var request = new LoginRequest(username, password);
        return makeRequest("POST", path, null, request, LoginResult.class);
    }

    public LogoutResult logout(String authToken) throws ResponseException {
        var path = "/session";
        var request = new LogoutRequest(authToken);
        return makeRequest("DELETE", path, authToken, request, LogoutResult.class);
    }


    //game routes
    public ListGamesResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        var request = new ListGamesRequest();
        return makeRequest("GET", path, authToken, null, ListGamesResult.class);
    }

    public ViewGameResult viewGame(String authToken, int gameID) throws ResponseException {
        var path = "/board";
        var request = new ViewGameRequest(gameID);
        return makeRequest("GET", path, authToken, null, ViewGameResult.class);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        return makeRequest("POST", path, authToken, request, CreateGameResult.class);
    }

    public JoinGameResult joinGame(String authToken, String username, ChessGame.TeamColor playerColor, int gameID) throws ResponseException {
        var path = "/game";
        var request = new JoinGameRequest(username, playerColor, gameID);
        return makeRequest("PUT", path, authToken, request, JoinGameResult.class);
    }



    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(authToken != null){
                http.setRequestProperty("Authorization", authToken);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}