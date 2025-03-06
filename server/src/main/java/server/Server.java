package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import server.service.ClearService;
import server.service.GameService;
import server.service.UserService;
import server.service.request.LoginRequest;
import server.service.request.LogoutRequest;
import server.service.request.RegisterRequest;
import spark.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.before;
import static spark.Spark.halt;

public class Server {

    UserService userService = new UserService();
    GameService gameService = new GameService();
    ClearService clearService = new ClearService();

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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void createRoutes(){
        //user routes
        //clear
        Spark.delete("/db", (req, res) -> new Gson().toJson(clearService.clearAllData(userService, gameService)));
        //register
        Spark.post("/user", this::registerHandler);
        //login
        Spark.post("/session", this::loginHandler);
        //logout
        Spark.delete("/session", this::logoutHandler);

    }

    private void authenticateHandler(Request req, Response res) {//return type might need to be Object
        String authToken = req.headers("authorization");
        boolean authenticated = userService.authenticate(authToken);

        if(!authenticated){
            halt(401, "not authorized");
        }
    }

    public Object errorHandler(Exception e, Request req, Response res) throws RuntimeException {
        //pass runtime exceptions up
        if(e instanceof RuntimeException){
            throw (RuntimeException)e;
        }

        //map checked exception messages to their respective error codes
        HashMap<String, Integer> errorMessageToCode = new HashMap<String, Integer>();
        errorMessageToCode.put("bad request", 400);
        errorMessageToCode.put("Some required fields are missing", 400);
        errorMessageToCode.put("unauthorized", 401);
        errorMessageToCode.put("User Not Found", 401);
        errorMessageToCode.put("already taken", 403);

        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");

        int errorCode = 500;
        for(String errorMessage : errorMessageToCode.keySet()){
            if(errorMessage.equals(e.getMessage())){
                errorCode = errorMessageToCode.get(errorMessage);
            }
        }
        res.status(errorCode);
        res.body(body);
        return body;
    }

    private Object registerHandler(Request req, Response res){
        //get and deserialize body
        RegisterRequest registerRequest = deserialize(req.body(), RegisterRequest.class);
                //send req data to service class, operate on it, return serialized Json response
        try {
            anyFieldBlank(registerRequest);
            return new Gson().toJson(userService.register(registerRequest));
        }
        catch (Exception e){
            return errorHandler(e, req, res);
        }
    }

    private Object loginHandler(Request req, Response res){
        //get and deserialize body
        LoginRequest loginRequest = deserialize(req.body(), LoginRequest.class);
        //send req data to service class, operate on it, return serialized Json response
        try{
            anyFieldBlank(loginRequest);
            return new Gson().toJson(userService.login(loginRequest));
        }
        catch (Exception e){
            return errorHandler(e, req, res);
        }
    }

    private Object logoutHandler(Request req, Response res){
        LogoutRequest logoutRequest = deserialize((req.body()), LogoutRequest.class);
        try{
            anyFieldBlank(logoutRequest);
            authenticateHandler(req, res);


        } catch (Exception e) {
            return errorHandler(e, req, res);
        }

        return null;
    }

    private <T extends Record> void anyFieldBlank(T record) throws DataInputException{
        boolean hasBlankField = false;

        if(record == null){ //if entire record is null then consider a field to be blank
            hasBlankField = true;
            throw new DataInputException("Some required fields are missing");
        }

        Field[] fields = record.getClass().getDeclaredFields();
        for(var field : fields){
            field.setAccessible(true);
            try{
                Object value = field.get(record);
                if(value == null || (value instanceof String && ((String)value).isBlank())){
                //if the object value is null or is a string and blank
                    hasBlankField = true;
                    break;
                }
            }
            catch(IllegalAccessException e){
                hasBlankField = true;
                break;
            }
        }
        if(hasBlankField){
            throw new DataInputException("Some required fields are missing");
        }
    }

    private static <T extends Record> T deserialize(String json, Class<T> yourClass){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        T object = gson.fromJson(json, yourClass);

        return object;
    }

}
