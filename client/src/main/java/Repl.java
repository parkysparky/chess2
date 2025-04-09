import exception.ResponseException;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) throws ResponseException {
        client = new Client(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to jank chess");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if(client.getState() == State.LOGGED_OUT){
                printPreloginMessage();
            }
            else{
                printPostloginMessage();
            }

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

    private void printPreloginMessage(){
        String loopMessage = "Please " +
                MATCH_CONSOLE_IN + "login " + RESET_FORMATTING + "or type " +
                MATCH_CONSOLE_IN + "help" + RESET_FORMATTING;

        System.out.println(loopMessage);
    }

    private void printPostloginMessage(){
        String loopMessage = SET_TEXT_FAINT + "[@" + SET_TEXT_COLOR_BLUE + client.getUsername() + RESET_FORMATTING + "]";

        System.out.println(loopMessage);
    }
}
