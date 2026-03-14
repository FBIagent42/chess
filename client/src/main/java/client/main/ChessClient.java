package client.main;



import model.requests.RegisterRequest;
import model.resulsts.RegisterResult;
import ui.State;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String auth;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.print(" Welcome to the Chess server");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                switch (state){
                    case LOGGED_OUT -> result = loggedOutEval(line);
                    case LOGGED_IN ->  result = loggedInEval(line);
                    case IN_GAME -> result = inGameEval(line);
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String inGameEval(String input){
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "move" -> move(params);
            case "highlight" -> highlight(params);
            case "redraw" -> redraw();
            case "resign" -> resign();
            case "leave" -> leave();
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Not a valid command\n";
        };
    }

    public String leave(){
        state = State.LOGGED_IN;
        return RESET + "You left the game";
    }

    public String resign(){
        state = State.LOGGED_IN;
        return RESET + "You have resigned";
    }

    public String redraw(){
        return RESET + "We have redrawn the board";
    }

    public String highlight(String... params){
        if(params.length == 1){
            String piece = params[0];
            if(piece.length() != 2){
                return SET_TEXT_COLOR_RED + "position need to be in format \"a4\"\n";
            }

            return RESET + String.format("List of possible moves for piece at %s", piece);
        }
        if(params.length < 1) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many params\n";
        }
    }

    public String move(String... params){
        if(params.length == 2 || params.length == 3){
            String start = params[0];
            String end = params[1];
            String promote;
            if(start.length() != 2 || end.length() != 2){
                return SET_TEXT_COLOR_RED + "moves need to be in format \"a4\"\n";
            }
            if(params.length == 3){
                promote = params[2];
                return RESET + String.format("moved pawn from %s to %s promoting to a %s", start, end, promote);
            }

            return RESET + String.format("moved piece from %s to %s.", start, end);
        }
        if(params.length < 2) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many params\n";
        }
    }

    public String loggedInEval(String input){
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Not a valid command\n";
        };
    }

    public String logout(){
        state = State.LOGGED_OUT;
        return RESET + "You have been logged out\n";
    }

    public String observe(String... params){
        if(params.length == 1){
            state = State.IN_GAME;
            String id = params[0];
            return RESET + String.format("You are observing game #%s\n.", id);
        }
        if(params.length < 1) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many params\n";
        }
    }

    public String join(String... params){
        if(params.length < 2) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        }
         if (!params[1].equals("white") && !params[1].equals("black")) {
            return SET_TEXT_COLOR_RED + "Color must be White or Black\n";
        }
        if(params.length == 2){
            state = State.IN_GAME;
            String id = params[0];
            String color = params[1];
            return RESET + String.format("You join game #%s as %s\n.", id, color);
        }
        return SET_TEXT_COLOR_RED + "Too many params\n";
    }

    public String listGames(){
        return RESET + "Here is a bunch of games\n";
    }

    public String createGame(String... params){
        if(params.length == 1){
            String name = params[0];
            return RESET + String.format("You created a game named %s\n.", name);
        }
        if(params.length < 1) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        }else {
            return SET_TEXT_COLOR_RED + "Too many params\n";
        }
    }

    public String loggedOutEval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            case "help" -> help();
            default -> SET_TEXT_COLOR_RED + "Not a valid command\n";
        };
    }

    public String register(String... params){
        if(params.length == 3){
            try{
                RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
                RegisterResult result = server.register(request);
                auth = result.authToken();
            } catch (ResponseException e) {
                return SET_TEXT_COLOR_RED + e.getMessage() + "\n";
            }
            state = State.LOGGED_IN;
            String username = params[0];
            return RESET + String.format("You registered as %s\n.", username);
        }
        if(params.length < 3) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        }else {
            return SET_TEXT_COLOR_RED + "Too many params\n";
        }
    }

    public String login(String... params){
        if (params.length == 2) {
            state = State.LOGGED_IN;
            String username = params[0];
            return RESET + String.format("You logged in as %s.\n", username);
        }
        if(params.length < 2) {
            return SET_TEXT_COLOR_RED + "needed more params\n";
        }else {
            return SET_TEXT_COLOR_RED + "Too many params\n";
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + "[" + state +  "] >>> " + SET_TEXT_COLOR_GREEN);
    }

    private String help(){
        System.out.print("\n" + RESET);
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + "register <Username> <Password> <Email> " + RESET + "-create new account\n" +
                    SET_TEXT_COLOR_BLUE + "login <Username> <Password> " + RESET + "-login to server\n" +
                    SET_TEXT_COLOR_BLUE + "quit " + RESET + "-exit program\n" +
                    SET_TEXT_COLOR_BLUE + "help " + RESET + "-list possible commands\n";
        } else if (state == State.LOGGED_IN) {
            return SET_TEXT_COLOR_BLUE + "create <Game Name> " + RESET + "-create new game\n" +
                    SET_TEXT_COLOR_BLUE + "list " + RESET + "-list current games\n" +
                    SET_TEXT_COLOR_BLUE + "join <Game ID> [White/Black] " + RESET + "-join game as color\n" +
                    SET_TEXT_COLOR_BLUE + "observe <Game ID> " + RESET + "-observe current game\n" +
                    SET_TEXT_COLOR_BLUE + "help " + RESET + "-list possible commands\n" +
                    SET_TEXT_COLOR_BLUE + "logout " + RESET + "-logout of server\n";
        }
        return SET_TEXT_COLOR_BLUE + "move <Start> <End> <optional Promotion> " + RESET + "-make chess move\n" +
                SET_TEXT_COLOR_BLUE + "highlight <Position> " + RESET + "-list possible moves for selected piece\n" +
                SET_TEXT_COLOR_BLUE + "redraw " + RESET + "-redraw current board\n" +
                SET_TEXT_COLOR_BLUE + "resign " + RESET + "-resign from game\n" +
                SET_TEXT_COLOR_BLUE + "help " + RESET + "-list possible commands\n" +
                SET_TEXT_COLOR_BLUE + "leave " + RESET + "-leave current game\n";
    }
}
