import chess.ChessGame;
import exception.ResponseException;
import model.GameInfo;
import server.facade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class Client {

    private final ServerFacade serverFacade;

    private State state = State.LOGGED_OUT;

    private String username;
    private String authToken;

    private Map<Integer, Integer> gameOrder;

    public Client(String url) throws ResponseException {
        serverFacade = new ServerFacade(url);
    }


    public String eval(String input){
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                //pre login
                case "login" -> login(params);
                case "register" -> register(params);
                //post login
                case "logout" -> logout();
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
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
            setUsername(params[0]);
            var password = params[1];
            setAuthToken(serverFacade.login(getUsername(), password).authToken());

            setState(State.LOGGED_IN);

            return String.format("You signed in as " + SET_TEXT_COLOR_BLUE + SET_TEXT_FAINT + "%s\n" + RESET_FORMATTING, username);
        }
        throw new ResponseException(400, "Expected: <" + MATCH_CONSOLE_IN + "username" + RESET_FORMATTING +
                "> <" + MATCH_CONSOLE_IN + "password" + RESET_FORMATTING + ">\n");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            setState(State.LOGGED_IN);
            setUsername(params[0]);
            var password = params[1];
            var email = params[2];

            setAuthToken(serverFacade.register(username, password, email).authToken());

            return String.format("You signed in as " + SET_TEXT_COLOR_BLUE + "%s\n" + RESET_FORMATTING, username);
        }
        throw new ResponseException(400, "Expected: <" + MATCH_CONSOLE_IN + "username" + RESET_FORMATTING +
                "> <" + MATCH_CONSOLE_IN + "password" + RESET_FORMATTING + "> <" +
                MATCH_CONSOLE_IN + "email" + RESET_FORMATTING + ">\n");
    }

    public String logout() throws ResponseException {
        setState(State.LOGGED_OUT);
        setUsername(null);

        serverFacade.logout(authToken);

        return "Signed Out\n";
    }

    public String createGame(String... params) throws ResponseException {
        assertLoggedIn();
        StringBuilder gameName = new StringBuilder();
        for(var word : params){
            gameName.append(word).append(" ");
        }
        if (!gameName.isEmpty()) {
            gameName.deleteCharAt(gameName.length() - 1);
        }
        if (gameName.length() > 64){
            throw new ResponseException(400, "Game name cannot exceed 64 characters\n");
        }


        int gameID = serverFacade.createGame(authToken, gameName.toString()).gameID();

        return "Game " + SET_TEXT_COLOR_MAGENTA + SET_TEXT_UNDERLINE + gameName + RESET_FORMATTING + " created. ID# " +
                SET_TEXT_COLOR_MAGENTA + gameID + RESET_FORMATTING + "\n";
    }

    public String listGames() throws ResponseException {
        assertLoggedIn();

        HashSet<GameInfo> gameList = serverFacade.listGames(authToken).games();

        StringBuilder output = new StringBuilder();
        gameOrder.clear();

        int count = 0;
        for(var gameInfo : gameList){
            count++;

            String gameName = gameInfo.gameName();

            String whitePlayer = setUsernameByColor(ChessGame.TeamColor.WHITE, gameInfo);
            String blackPlayer = setUsernameByColor(ChessGame.TeamColor.BLACK, gameInfo);

            output.append(listFormatter(count, gameName, whitePlayer, blackPlayer));
            gameOrder.put(count, gameInfo.gameID());
        }

        return output.toString();
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
            returnString.append(MATCH_CONSOLE_IN + "login " + RESET_FORMATTING);
            returnString.append("<" + MATCH_CONSOLE_IN + "username" + RESET_FORMATTING + "> <");
            returnString.append(MATCH_CONSOLE_IN + "password" + RESET_FORMATTING + ">\n");

            returnString.append(MATCH_CONSOLE_IN + "register " + RESET_FORMATTING);
            returnString.append("<" + MATCH_CONSOLE_IN + "username" + RESET_FORMATTING + "> <");
            returnString.append(MATCH_CONSOLE_IN + "password" + RESET_FORMATTING + "> <");
            returnString.append(MATCH_CONSOLE_IN + "email" + RESET_FORMATTING + ">\n");

            returnString.append(MATCH_CONSOLE_IN + "quit\n" + RESET_FORMATTING);
        }
        else {
            returnString.append(MATCH_CONSOLE_IN + "listGames\n");

            returnString.append(MATCH_CONSOLE_IN + "createGame " + RESET_FORMATTING);
            returnString.append("<" + MATCH_CONSOLE_IN + "game name" + RESET_FORMATTING + ">\n");

            returnString.append(MATCH_CONSOLE_IN + "playGame " + RESET_FORMATTING);
            returnString.append("<" + MATCH_CONSOLE_IN + "gameNumber" + RESET_FORMATTING + "> <");
            returnString.append(MATCH_CONSOLE_IN + "BLACK|WHITE" + RESET_FORMATTING + ">\n");

            returnString.append(MATCH_CONSOLE_IN + "observeGame " + RESET_FORMATTING);
            returnString.append("<" + MATCH_CONSOLE_IN + "gameNumber" + RESET_FORMATTING + ">\n");

            returnString.append(MATCH_CONSOLE_IN + "logout\n");

            returnString.append("quit\n" + RESET_FORMATTING);
        }

        return returnString.toString();
    }


    public State getState(){
        return state;
    }

    private void setState(State newState){
        state = newState;
    }

    public String getUsername(){
        if (serverFacade == null) {
            return (String) null; //this may need different null handling
        }
        return username;
    }

    private void setUsername(String newUsername){
        username = newUsername;
    }

    private void setAuthToken(String newAuthToken){
        authToken = newAuthToken;
    }


    private String setUsernameByColor(ChessGame.TeamColor color, GameInfo game){
        String username = "available";
        switch (color) {
            case BLACK -> {
                if(game.blackUsername() != null){
                    username = game.blackUsername();
                }
            }
            case WHITE -> {
                if (game.whiteUsername() != null) {
                    username = game.whiteUsername();
                }
            }
        }

        return username;
    }

    private String listFormatter(int gameID, String gameName, String whiteUsername, String blackUsername){
        StringBuilder output =  new StringBuilder();

        output.append(SET_TEXT_BOLD + "game# " + RESET_FORMATTING);
        output.append(SET_TEXT_COLOR_MAGENTA + gameID + "  " + RESET_FORMATTING);

        output.append(SET_TEXT_BOLD + "gameName: " + RESET_FORMATTING);
        output.append(SET_TEXT_COLOR_MAGENTA + SET_TEXT_UNDERLINE + gameName + RESET_FORMATTING + "  ");

        output.append(SET_TEXT_BOLD + "White: " + RESET_FORMATTING);
        if(getUsername().equals(whiteUsername)){
            output.append(SET_TEXT_COLOR_BLUE + whiteUsername + RESET_FORMATTING + "  ");
        } else if (whiteUsername.equals("available")){
            output.append(SET_TEXT_BOLD + "[" + RESET_FORMATTING);
            output.append(SET_TEXT_COLOR_LIGHT_GREY + whiteUsername + RESET_FORMATTING);
            output.append(SET_TEXT_BOLD + "]  " + RESET_FORMATTING);
        }
        else{
            output.append(SET_TEXT_COLOR_PURPLE + whiteUsername + RESET_FORMATTING + "  ");
        }

        output.append(SET_TEXT_BOLD + "Black: " + RESET_FORMATTING);
        if(getUsername().equals(blackUsername)){
            output.append(SET_TEXT_COLOR_BLUE + blackUsername + RESET_FORMATTING + " ");
        } else if (blackUsername.equals("available")){
            output.append(SET_TEXT_BOLD + "[" + RESET_FORMATTING);
            output.append(SET_TEXT_COLOR_LIGHT_GREY + blackUsername + RESET_FORMATTING);
            output.append(SET_TEXT_BOLD + "] " + RESET_FORMATTING);
        }
        else{
            output.append(SET_TEXT_COLOR_PURPLE + blackUsername + RESET_FORMATTING + " ");
        }

        output.append("\n");

        return output.toString();
    }


    private void assertLoggedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(401, "You must sign in\n");
        }
    }
}
