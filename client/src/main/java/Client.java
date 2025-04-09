import exception.ResponseException;
import server.ServerFacade;
import ui.EscapeSequences;

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


    public String help() {
        StringBuilder returnString = new StringBuilder();
        if (state == State.LOGGED_OUT) {
            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "login " + EscapeSequences.RESET_FORMATTING);
            returnString.append("<" + EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "username" + EscapeSequences.RESET_FORMATTING + "> <");
            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "password" + EscapeSequences.RESET_FORMATTING + ">\n");

            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "register " + EscapeSequences.RESET_FORMATTING);
            returnString.append("<" + EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "username" + EscapeSequences.RESET_FORMATTING + "> <");
            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "password" + EscapeSequences.RESET_FORMATTING + "> <");
            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "email" + EscapeSequences.RESET_FORMATTING + ">\n");

            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "quit\n" + EscapeSequences.RESET_FORMATTING);
        }
        else {
            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "listGames\n");

            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "createGame " + EscapeSequences.RESET_FORMATTING);
            returnString.append("<" + EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "game name" + EscapeSequences.RESET_FORMATTING + ">\n");

            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "playGame " + EscapeSequences.RESET_FORMATTING);
            returnString.append("<" + EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "gameNumber" + EscapeSequences.RESET_FORMATTING + "> <");
            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "BLACK|WHITE" + EscapeSequences.RESET_FORMATTING + ">\n");

            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "observeGame " + EscapeSequences.RESET_FORMATTING);
            returnString.append("<" + EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "game name" + EscapeSequences.RESET_FORMATTING + ">\n");

            returnString.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "logout\n");

            returnString.append("quit\n" + EscapeSequences.RESET_FORMATTING);
        }

        return returnString.toString();
    }


    private void assertLoggedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(401, "You must sign in");
        }
    }
}
