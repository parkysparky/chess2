import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        if (0 < args.length) {
            Server server = new Server();
            int serverPort = 8080;
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port: 8080");
            }
            server.run(serverPort);
        }
        else {
            Server server = new Server();
            server.run(8080);
        }
    }
}