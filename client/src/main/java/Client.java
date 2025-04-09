import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class Client {

    private ServerFacade serverFacade;
    private Repl repl;
    private State state = State.LOGGED_OUT;
    private String username;

    public Client(String url, Repl repl) throws ResponseException {
        serverFacade = new ServerFacade(url);
        this.repl = repl;
    }


    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //pre login
                case "login" -> login(params);
                case "register" -> register(params);
                //post login
                case "logout" -> logout();
                case "createGame" -> createGame(params);
                case "listGames" -> listGames();
                case "playgame" -> playGame(params);
                case "observegame" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }


    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.LOGGED_IN;
            username = params[0];
            var password = params[1];

            serverFacade.login(username, password);

            return String.format("You signed in as %s.", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params){
        return null;
    }

    public String listGames(String... params){
        return null;
    }

    public String logout(){
        return null;
    }

    public String createGame(String... params){
        return null;
    }

    public String playGame(String... params){
        return null;
    }

    public String observeGame(String... params){
        return null;
    }

    public String quit(){
        return null;
    }


    public String help() {
        if (state == State.LOGGED_OUT) {
            return """
                    - logIn <username> <password>
                    - register <username> <password> <email>
                    - quit
                    """;
        }
        return """
                - listGames
                - createGame <game name>
                - playGame <gameID> <BLACK|WHITE>
                - observeGame <game name>
                - logOut
                - quit
                """;
    }


    private void assertLoggedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(401, "You must sign in");
        }
    }
}
