package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.service.request.RegisterRequest;
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
        //register
        Spark.post("/user", (req, res) -> {
            //get and deserialize body
            RegisterRequest registerRequest = deserialize(req.body(), RegisterRequest.class);
            //send req data to service class

            //receive service class response
            //serialize service class stuff
            //put service class stuff into response object

            //return response object
            return res;
        });



    }

    private static <T extends Record> T deserialize(String json, Class<T> yourClass){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        T object = gson.fromJson(json, yourClass);

        return object;
    }

}
