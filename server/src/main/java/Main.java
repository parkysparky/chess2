import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int portNumber = 8080;
        if (0 < args.length) {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port: 8080");
            }
        }
        server.run(portNumber);
    }
}