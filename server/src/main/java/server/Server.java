package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import server.service.UserService;
import server.service.request.RegisterRequest;
import server.service.result.RegisterResult;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes(){
        //TODO: write a before method for authentication

        UserService userService = new UserService();

        //register
        Spark.post("/user", (req, res) -> {
            //get and deserialize body
            RegisterRequest registerRequest = deserialize(req.body(), RegisterRequest.class);

            //send req data to service class and store it in service response object
            try{
                return new Gson().toJson(userService.register(registerRequest));

            }
            catch (DataAccessException e){
                return null;
            }
        });



    }

    private static <T extends Record> T deserialize(String json, Class<T> yourClass){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        T object = gson.fromJson(json, yourClass);

        return object;
    }

}
