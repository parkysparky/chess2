package server;

import com.google.gson.Gson;
import exception.ErrorResponse;
import exception.ResponseException;
import server.request.ClearRequest;
import server.request.RegisterRequest;
import server.result.ClearResult;
import server.result.RegisterResult;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    /// Create endpoints here
    /*  Example implementation
     public Pet addPet(Pet pet) throws ResponseException {
        var path = "/pet";
        return this.makeRequest("POST", path, pet, Pet.class);
     }

    public void deletePet(int id) throws ResponseException {
        var path = String.format("/pet/%s", id);
        this.makeRequest("DELETE", path, null, null);
    }

    public void deleteAllPets() throws ResponseException {
        var path = "/pet";
        this.makeRequest("DELETE", path, null, null);
    }
     */
    ///endpoints to implement
    /*
        //clear
        Spark.delete("/db", this::clearHandler);

        //user routes
        //register
        Spark.post("/user", this::registerHandler);
        //login
        Spark.post("/session", this::loginHandler);
        //logout
        Spark.delete("/session", this::logoutHandler);

        //game routes
        //listGames
        Spark.get("/game", this::listGamesHandler);
        //createGame
        Spark.post("/game", this::createGameHandler);
        //joinGame
        Spark.put("/game", this::joinGameHandler);
     */

    public void clear() throws ResponseException {
        var path = "/clear";
        makeRequest("DELETE", path, new ClearRequest(), ClearResult.class);
    }

    //user routes
    public RegisterResult register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        var request = new RegisterRequest(username, password, email);
        return makeRequest("POST", path, request, RegisterResult.class);
    }






    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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