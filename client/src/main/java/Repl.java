import exception.ResponseException;

import java.util.Scanner;
import ui.EscapeSequences;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) throws ResponseException {
        client = new Client(serverUrl, this);
    }

    public void run() {
        printWelcomeMessage();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printWelcomeMessage(){
        String welcomeMessage = "Welcome to jank chess\nPlease " +
                EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "login " +
                EscapeSequences.RESET_FORMATTING + "or type " +
                EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "help" +
                EscapeSequences.RESET_FORMATTING;

        System.out.println(welcomeMessage);
    }
}
