package server;

import com.google.gson.*;
import dataaccess.DataAccessException;
import server.request.*;
import server.service.GameService;
import server.service.UserService;
import spark.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Server {
    UserService userService;
    GameService gameService;
    final int port;

    public Server() {
        this.port = 8080;
        try{
            userService = new UserService();
            gameService = new GameService();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Server(int port) {
        this.port = port;
        try{
            userService = new UserService();
            gameService = new GameService();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Server(boolean useMySQL) {
        this.port = 8080;
        try {
            userService = new UserService(useMySQL);
            gameService = new GameService(useMySQL);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Server(boolean useMySQL, int port) {
        this.port = port;
        try {
            userService = new UserService(useMySQL);
            gameService = new GameService(useMySQL);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        this.createRoutes();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public int getPort(){
        return port;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void createRoutes() {
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
    }

    private void authenticateHandler(String authToken) throws DataInputException {//return type might need to be Object
        boolean authenticated = userService.authenticate(authToken) != null;

        if (!authenticated) {
            throw new DataInputException("unauthorized");
        }
    }

    public Object errorHandler(Exception e, Request req, Response res) throws RuntimeException {
        //pass runtime exceptions up
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }

        //map checked exception messages to their respective error codes
        HashMap<String, Integer> errorMessageToCode = new HashMap<>();
        errorMessageToCode.put("bad request", 400);
        errorMessageToCode.put("Nothing to update", 400);
        errorMessageToCode.put("Some required fields are missing", 400);
        errorMessageToCode.put("Username cannot be null or blank", 400);
        errorMessageToCode.put("forbidden", 401);
        errorMessageToCode.put("unauthorized", 401);
        errorMessageToCode.put("User Not Found", 401);
        errorMessageToCode.put("already taken", 403);

        int errorCode = 500;
        for (String errorMessage : errorMessageToCode.keySet()) {
            if (errorMessage.equals(e.getMessage())) {
                errorCode = errorMessageToCode.get(errorMessage);
            }
        }

        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "status", errorCode));
        res.type("application/json");

        res.status(errorCode);
        res.body(body);
        return body;
    }

    private Object clearHandler(Request req, Response res) throws DataAccessException {
        gameService.clear();
        userService.clear();
        return new Gson().toJson(new ClearRequest());
    }

    private Object registerHandler(Request req, Response res) {
        //get and deserialize body
        RegisterRequest registerRequest = deserialize(req.body(), RegisterRequest.class);
        //send req data to service class, operate on it, return serialized Json response
        try {
            anyFieldBlank(registerRequest);
            return new Gson().toJson(userService.register(registerRequest));
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    private Object loginHandler(Request req, Response res) {
        //get and deserialize body
        LoginRequest loginRequest = deserialize(req.body(), LoginRequest.class);
        //send req data to service class, operate on it, return serialized Json response
        try {
            anyFieldBlank(loginRequest);
            return new Gson().toJson(userService.login(loginRequest));
        } catch (Exception e) {

            return errorHandler(e, req, res);
        }
    }

    private Object logoutHandler(Request req, Response res) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization"));
        try {
            authenticateHandler(logoutRequest.authToken());
            return new Gson().toJson(userService.logout(logoutRequest));
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    private Object listGamesHandler(Request req, Response res) {
        //send req data to service class, operate on it, return serialized Json response
        try {
            authenticateHandler(req.headers("authorization"));
            return new Gson().toJson(gameService.listGames(new ListGamesRequest()));
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    private Object createGameHandler(Request req, Response res) {
        //get and deserialize body
        CreateGameRequest createGameRequest = deserialize(req.body(), CreateGameRequest.class);
        //send req data to service class, operate on it, return serialized Json response
        try {
            anyFieldBlank(createGameRequest);
            authenticateHandler(req.headers("authorization"));
            return new Gson().toJson(gameService.createGame(createGameRequest));////actually implement this method
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    private Object joinGameHandler(Request req, Response res) {
        //send req data to service class, operate on it, return serialized Json response
        try {
            authenticateHandler(req.headers("authorization"));

            //populate joinGameRequest
            String username = userService.authenticate(req.headers("authorization"));
            JoinGameRequestBody joinGameRequestBody = deserialize(req.body(), JoinGameRequestBody.class);
//            var joinGameRequestBody = new Gson().toJson(req.body(), JoinGameRequestBody.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(username, joinGameRequestBody.playerColor(), joinGameRequestBody.gameID());

            //input validation
            anyFieldBlank(joinGameRequestBody);

            return new Gson().toJson(gameService.joinGame(joinGameRequest));
        } catch (Exception e) {
            return errorHandler(e, req, res);
        }
    }

    private <T extends Record> void anyFieldBlank(T record) throws DataInputException {
        boolean hasBlankField = false;
        if (record == null) { //if entire record is null then consider a field to be blank
            throw new DataInputException("Some required fields are missing");
        }

        Field[] fields = record.getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(record);
                if (value == null || (value instanceof String && ((String) value).isBlank())) {
                    //if the object value is null or is a string and blank
                    hasBlankField = true;
                    break;
                }
            } catch (IllegalAccessException e) {
                hasBlankField = true;
                break;
            }
        }
        if (hasBlankField) {
            throw new DataInputException("Some required fields are missing");
        }
    }

    private static <T extends Record> T deserialize(String json, Class<T> yourClass) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.fromJson(json, yourClass);
    }

}
