import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameInfo;
import server.facade.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Client {

    private final ServerFacade serverFacade;

    private State state = State.LOGGED_OUT;

    private String username;
    private String authToken;

    private Map<Integer, Integer> gameOrder = new HashMap<>();

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

    public String playGame(String... params) throws ResponseException {
        assertLoggedIn();
        //validate gameID
        int gameID = getGameID(params[0]);
        gameIDValid(gameID);
        //validate color selection
        if(!params[1].equalsIgnoreCase("white") && !params[1].equalsIgnoreCase("black")){
            throw new ResponseException(400, "please choose to play as black or white");
        }
        ChessGame.TeamColor playerColor = ChessGame.TeamColor.valueOf(params[1]);

        serverFacade.joinGame(authToken, username, playerColor, gameID);

        //TODO write function to get game, or change DAO to return gameData instead of gameInfo

        return getBoard(game, playerColor);
    }

    public String observeGame(String... params) throws ResponseException {
        assertLoggedIn();

        int gameID = getGameID(params[0]);
        gameIDValid(gameID);

        return getBoard(game, null);
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


    private int getGameID(String input) throws ResponseException {
        int gameID;
        try{
            gameID = Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            throw new ResponseException(400, "please enter a valid game number as a number (like '3')");
        }

        return gameID;
    }

    private void gameIDValid(int gameID) throws ResponseException {
        if(gameOrder.isEmpty()){
            throw new ResponseException(400, "Please list games before attempting to access a game");
        }
        if(gameID > 0 && gameOrder.size() >= gameID){
            throw new ResponseException(400, "Please select a valid game#");
        }
    }


    private String getBoard(ChessGame game, ChessGame.TeamColor playerColor){
        return switch (playerColor) {
            case BLACK -> blackBoard(game);
            case null, default -> whiteBoard(game);
        };
    }

    private String whiteBoard(ChessGame game){
        StringBuilder returnString = new StringBuilder();
        for(int i = 8; i > 0; i--){
            returnString.append(SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY).append(i);

            for(int j = 1; j <= 8; j++){
                returnString.append(SET_TEXT_COLOR_LIGHT_GREY);
                if((i+j)%2 == 0){
                    returnString.append(SET_BG_COLOR_WHITE);
                }
                else{
                    returnString.append(SET_BG_COLOR_BLACK);
                }
                returnString.append(chessPieceToUnicode(game.getBoard().getPiece(new ChessPosition(i, j))));
            }
        }
        returnString.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        returnString.append(EMPTY + "a" + EMPTY + "b" + EMPTY + "c" + EMPTY + "d" + EMPTY + "e" + EMPTY + "f" + EMPTY + "g" + EMPTY + "h" + EMPTY);
        returnString.append(RESET_FORMATTING);

        return returnString.toString();
    }

    private String blackBoard(ChessGame game){
        StringBuilder returnString = new StringBuilder();
        for(int i = 1; i <= 8; i++){
            returnString.append(SET_TEXT_COLOR_BLACK + SET_BG_COLOR_LIGHT_GREY).append(i);

            for(int j = 8; j >= 1; j--){
                returnString.append(SET_TEXT_COLOR_LIGHT_GREY);
                if((i+j)%2 == 0){
                    returnString.append(SET_BG_COLOR_WHITE);
                }
                else{
                    returnString.append(SET_BG_COLOR_BLACK);
                }
                returnString.append(chessPieceToUnicode(game.getBoard().getPiece(new ChessPosition(i, j))));
            }
        }
        returnString.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        returnString.append(EMPTY + "a" + EMPTY + "b" + EMPTY + "c" + EMPTY + "d" + EMPTY + "e" + EMPTY + "f" + EMPTY + "g" + EMPTY + "h");
        returnString.append(RESET_FORMATTING);

        return returnString.toString();
    }

    private String chessPieceToUnicode(ChessPiece piece){
        String returnValue = EMPTY;
        if(piece == null){
            return returnValue;
        }

        Map<String, String> pieceNameToSymbol = new HashMap<>();
        pieceNameToSymbol.put("BLACK PAWN", BLACK_PAWN);
        pieceNameToSymbol.put("BLACK BISHOP", BLACK_BISHOP);
        pieceNameToSymbol.put("BLACK KNIGHT", BLACK_KNIGHT);
        pieceNameToSymbol.put("BLACK ROOK", BLACK_ROOK);
        pieceNameToSymbol.put("BLACK QUEEN", BLACK_QUEEN);
        pieceNameToSymbol.put("BLACK KING", BLACK_KING);

        pieceNameToSymbol.put("WHITE PAWN", WHITE_PAWN);
        pieceNameToSymbol.put("WHITE BISHOP", WHITE_BISHOP);
        pieceNameToSymbol.put("WHITE KNIGHT", WHITE_KNIGHT);
        pieceNameToSymbol.put("WHITE ROOK", WHITE_ROOK);
        pieceNameToSymbol.put("WHITE QUEEN", WHITE_QUEEN);
        pieceNameToSymbol.put("WHITE KING", WHITE_KING);

        returnValue = pieceNameToSymbol.get(piece.toString());

        return returnValue;
    }



    private void assertLoggedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(401, "You must sign in\n");
        }
    }
}
