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
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Welcome to jank chess\nPlease ");
        messageBuilder.append(EscapeSequences.MATCH_TEXT_TO_CONSOLE_IN + "login ");
        messageBuilder.append(EscapeSequences.RESET_FORMATTING + "or type ");
        messageBuilder.append(EscapeSequences.SET_TEXT_COLOR_CONSOLE_GREEN + EscapeSequences.SET_TEXT_ITALIC + "help");

        messageBuilder.append(EscapeSequences.RESET_FORMATTING);

        System.out.println(messageBuilder);
    }
}
