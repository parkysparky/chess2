import dataaccess.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) {
        int portNumber = 8080;
        boolean useMySQL = true;
        if (0 < args.length) {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port: 8080");
            }
            if(1 < args.length) {
                String dbFormat = args[1];
                if(dbFormat.equalsIgnoreCase("memory")){
                    useMySQL = false;
                }
            }
        }
        Server server = new Server(useMySQL, portNumber);
        server.run(portNumber);
    }
}