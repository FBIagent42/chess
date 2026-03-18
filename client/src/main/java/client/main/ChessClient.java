package client.main;



import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import model.requests.*;
import model.resulsts.LoginResult;
import model.resulsts.RegisterResult;
import ui.State;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String auth;
    private GameData game;
    private String username;
    private String team;
    private final HashMap<Integer, Integer> gameMap = new HashMap<>();
    private static final String BACKGROUND = SET_TEXT_COLOR_BLACK + SET_BG_COLOR_DARK_GREY;
    private static final String WHITE_SPACE = SET_BG_COLOR_LIGHTER_GREY;
    private static final String BLACK_SPACE = SET_BG_COLOR_BLACK;
    private static final String WHITE_PIECE = SET_TEXT_COLOR_DARK_PURPLE;
    private static final String BLACK_PIECE = SET_TEXT_COLOR_RED;

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
            } catch (ResponseException e) {
                System.out.print(e.getMessage());
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
            return SET_TEXT_COLOR_RED + "Not enough inputs, requires: <Position>\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
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
            return SET_TEXT_COLOR_RED + "Not enough inputs, requires: <Start> <End> <optional Promotion>\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
        }
    }

    public String loggedInEval(String input) throws ResponseException {
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

    public String logout() throws ResponseException {
        try {
            server.logout(new LogoutRequest(auth));
        } catch (ResponseException e) {
            throw ResponseException.printCode(e);
        }
        username = null;
        state = State.LOGGED_OUT;
        return RESET + "You have been logged out\n";
    }

    public String observe(String... params) throws ResponseException {
        if(params.length == 1){
            String id = params[0];
            try {
                int gameID = Integer.parseInt(params[0]);
                Collection<GameData> games = server.listGames(new ListGamesRequest(auth)).games();
                for (GameData data : games) {
                    if (data.gameID() == gameID) {
                        game = data;
                    }
                }
            } catch (NumberFormatException e) {
                throw new ResponseException(400, SET_TEXT_COLOR_RED + "Invalid number: " + params[0]);
            }
            state = State.IN_GAME;
            team = "white";
            drawBoard();
            return RESET + String.format("You are observing game #%s\n.", id);
        }
        if(params.length < 1) {
            return SET_TEXT_COLOR_RED + "Not enough inputs, requires: <gameID>\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
        }
    }

    public String join(String... params) throws ResponseException {
        if(params.length < 2) {
            return SET_TEXT_COLOR_RED + "\"Not enough inputs, requires: <gameID> [white/black]\n";
        }
         if (!params[1].equalsIgnoreCase("white") && !params[1].equalsIgnoreCase("black")) {
            return SET_TEXT_COLOR_RED + "Color must be White or Black\n";
        }
        if(params.length == 2){
            int gameID;
            try{
                try {
                    gameID = Integer.parseInt(params[0]);
                } catch (NumberFormatException e) {
                    throw new ResponseException(400, "Invalid number: " + params[0]);
                }
                if(gameMap.containsKey(gameID)){
                    gameID = gameMap.get(gameID);
                }
                JoinGameRequest request = new JoinGameRequest(auth, params[1].toLowerCase(), gameID);
                server.joinGame(request);
                Collection<GameData> games = server.listGames(new ListGamesRequest(auth)).games();
                for(GameData data: games){
                    if(data.gameID() == gameID){
                        game = data;
                    }
                }
            } catch (ResponseException e) {
                throw ResponseException.printCode(e);
            }
            state = State.IN_GAME;
            String id = params[0];
            String color = params[1];
            if(color.equals("white")){
                team = color;
            } else{
                team = "black";
            }
            drawBoard();
            return RESET + String.format("You join game #%s as %s\n.", id, color);
        }
        return SET_TEXT_COLOR_RED + "Too many inputs\n";
    }

    public String listGames() throws ResponseException {
        StringBuilder gameList = new StringBuilder();
        try{
            ListGamesRequest request = new ListGamesRequest(auth);
            Collection<GameData> games = server.listGames(request).games();
            int i = 1;
            gameMap.clear();
            for(GameData data: games){
                String whiteUser;
                String blackUser;
                if(data.whiteUsername() == null){
                    whiteUser = "Open";
                } else{
                    whiteUser = data.whiteUsername();
                }
                if(data.blackUsername() == null){
                    blackUser = "Open";
                } else{
                    blackUser = data.blackUsername();
                }
                gameList.append(i)
                        .append(".  Game Name: ")
                        .append(data.gameName())
                        .append("   White: ")
                        .append(whiteUser)
                        .append("   Black: ")
                        .append(blackUser)
                        .append("\n");
                gameMap.put(i, data.gameID());
                i++;
            }
        } catch (ResponseException e) {
            throw ResponseException.printCode(e);
        }
        return RESET + gameList;
    }

    public String createGame(String... params){
        if(params.length == 1) {
            int gameID;
            try {
                CreateGameRequest request = new CreateGameRequest(params[0], auth);
                gameID = server.createGame(request).gameID();
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
            String name = params[0];
            return RESET + String.format("You created a game named %s with ID %s\n.", name, gameID);
        }
        if(params.length < 1) {
            return SET_TEXT_COLOR_RED + "\"Not enough inputs, requires: <gameName>\n";
        }else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
        }
    }

    public String loggedOutEval(String input) throws ResponseException {
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

    public String register(String... params) throws ResponseException {
        if(params.length == 3){
            try{
                RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
                RegisterResult result = server.register(request);
                auth = result.authToken();
            } catch (ResponseException e) {
                throw ResponseException.printCode(e);
            }
            state = State.LOGGED_IN;
            username = params[0];
            return RESET + String.format("You registered as %s\n.", username);
        }
        if(params.length < 3) {
            return SET_TEXT_COLOR_RED + "\"Not enough inputs, requires: <Username> <Password> <Email>\n";
        }else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            try{
                LoginRequest request = new LoginRequest(params[0], params[1]);
                LoginResult result = server.login(request);
                auth = result.authToken();
            } catch (ResponseException e) {
                throw ResponseException.printCode(e);
            }
            state = State.LOGGED_IN;
            username = params[0];
            return RESET + String.format("You logged in as %s.\n", username);
        }
        if(params.length < 2) {
            return SET_TEXT_COLOR_RED + "\"Not enough inputs, requires: <Username> <Password>\n";
        }else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
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
                    SET_TEXT_COLOR_BLUE + "join <Game ID> [white/black] " + RESET + "-join game as color\n" +
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

    private void drawBoard(){
        drawHeader();
        drawSquares();
        drawHeader();
    }

    private void drawSquares(){
        String[] numbers = {"8","7","6","5","4","3","2","1"};
        int color = 1;
        ChessPiece piece;
        ChessBoard board = game.game().getBoard();
        int start = 1;
        int way = 1;
        if(team != null && team.equals("black")){
            start = 8;
            way = -1;
        }
        for(int row = start; row <= 8 && row >= 1; row += way){
            System.out.print(BACKGROUND + " " + numbers[row - 1] + " ");
            for(int col = start; col <= 8 && col >= 1; col += way){
                piece = board.getPiece(new ChessPosition(row, col));
                if(color == 1){
                    System.out.print(WHITE_SPACE);
                } else{
                    System.out.print(BLACK_SPACE);
                }
                color *= -1;
                System.out.print(getPiece(piece));
            }
            color *= -1;
            System.out.println(BACKGROUND + " " + numbers[row - 1] + " " + RESET);
        }
    }

    private void drawHeader(){
        String[] letters = { "ａ", "ｂ", "ｃ", "ｄ", "ｅ", "ｆ", "ｇ", "ｈ" };

        if(team != null && team.equals("black")){
            Collections.reverse(Arrays.asList(letters));
        }

        StringBuilder header = new StringBuilder().append(BACKGROUND + "   ");
        for(String letter: letters){
            header.append(" ")
                    .append(letter)
                    .append(" ");
        }
        header.append("   " + RESET);
        System.out.println(header);
    }

    private String getPiece(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }
        String background;
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            background = WHITE_PIECE;
        } else{
            background = BLACK_PIECE;
        }
        return background + switch (piece.toString().toLowerCase()){
            case "k" -> BLACK_KING;
            case "q" -> BLACK_QUEEN;
            case "r" -> BLACK_ROOK;
            case "b" -> BLACK_BISHOP;
            case "n" -> BLACK_KNIGHT;
            case "p" -> BLACK_PAWN;
            default -> " ";
        };
    }
}
