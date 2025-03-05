package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import model.ErrorData;
import server.service.ClearService;
import server.service.GameService;
import server.service.UserService;
import server.service.request.RegisterRequest;
import server.service.result.RegisterResult;
import spark.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.route.HttpMethod.before;

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
        //TODO: write a before method for authentication

        //authenticate  filter
        before((req, res) -> {

        });

        //user routes
        Spark.post("/user", this::handleRegister);  //register
        Spark.delete("/db", (req, res) -> new Gson().toJson(clearService.clearAllData(userService, gameService)));  //clear
        //


    }

//    private Object handleAuthentication(Request req, Response res) {
//        boolean authenticated = false;
//
//        //authenticate
//
//        if(!authenticated){
//            halt(401, "not authorized");
//        }
//
//    }

    public Object errorHandler(Exception e, Request req, Response res) {
        HashMap<String, Integer> errorMessageToCode = new HashMap<String, Integer>();
        errorMessageToCode.put("bad request", 400);
        errorMessageToCode.put("unauthorized", 401);
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

    private Object handleRegister(Request req, Response res){
        //get and deserialize body
        RegisterRequest registerRequest = deserialize(req.body(), RegisterRequest.class);
        if(anyFieldBlank(registerRequest)){
            return errorHandler(new DataAccessException("bad request"), req, res);
        }

        //send req data to service class, operate on it, return serialized Json response
        try{
            return new Gson().toJson(userService.register(registerRequest));
        }
        catch (DataAccessException e){
            return errorHandler(e, req, res);
        }
    }

    private <T extends Record> boolean anyFieldBlank(T record){
        boolean hasBlankField = false;

        if(record == null){ //if entire record is null then consider a field to be blank
            hasBlankField = true;
            return hasBlankField;
        }

        Field[] fields = record.getClass().getDeclaredFields();
        for(var field : fields){
            field.setAccessible(true);
            try{
                Object value = field.get(record);
                if(value == null || (value instanceof String && ((String)value).isBlank())){
                    hasBlankField = true;
                    break;
                }
            }
            catch(IllegalAccessException e){
                hasBlankField = true;
                break;
            }
        }
        return hasBlankField;
    }

    private static <T extends Record> T deserialize(String json, Class<T> yourClass){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        T object = gson.fromJson(json, yourClass);

        return object;
    }

}
