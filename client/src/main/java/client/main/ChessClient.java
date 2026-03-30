package client.main;



import chess.*;
import client.main.websocket.NotificationHandler;
import client.main.websocket.WebSocketFacade;
import model.GameData;
import model.requests.*;
import model.resulsts.LoginResult;
import model.resulsts.RegisterResult;
import ui.State;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private State state = State.LOGGED_OUT;
    private String auth;
    private ChessGame game;
    private int gameID;
    private String username;
    private String team;
    private final HashMap<Integer, Integer> gameMap = new HashMap<>();
    private static final String BACKGROUND = SET_TEXT_COLOR_BLACK + SET_BG_COLOR_DARK_GREY;
    private static final String WHITE_SPACE = SET_BG_COLOR_LIGHTER_GREY;
    private static final String BLACK_SPACE = SET_BG_COLOR_BLACK;
    private static final String WHITE_PIECE = SET_TEXT_COLOR_DARK_PURPLE;
    private static final String BLACK_PIECE = SET_TEXT_COLOR_RED;

    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
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

    public void notify(ServerMessage notification) {
        switch (notification){
            case NotificationMessage message -> System.out.println(RESET + message);
            case ErrorMessage error -> System.out.println(SET_TEXT_COLOR_RED + error);
            case LoadGameMessage gameMessage -> game = gameMessage.getGame();
            default -> System.out.println(SET_TEXT_COLOR_RED + notification);
        }
        drawBoard();
        printPrompt();
    }

    public String inGameEval(String input) throws ResponseException {
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

    public String leave() throws ResponseException {
        ws.leave(auth, gameID);
        state = State.LOGGED_IN;
        game = null;
        team = null;
        return RESET + "You left the game";
    }

    public String resign() throws ResponseException {
        ws.resign(auth, gameID);
        return RESET + "You have resigned";
    }

    public String redraw(){
        System.out.print("\n\n");
        drawBoard();
        return RESET;
    }

    public String highlight(String... params){
        if(params.length == 1){
            String piece = params[0];
            if(piece.length() != 2){
                return SET_TEXT_COLOR_RED + "position need to be in format \"a4\"\n";
            }
            ChessPosition pos = parsePos(piece);
            drawBoard(game.validMoves(pos));
            return RESET;
        }
        if(params.length < 1) {
            return SET_TEXT_COLOR_RED + "Not enough inputs, requires: <Position>\n";
        } else {
            return SET_TEXT_COLOR_RED + "Too many inputs\n";
        }
    }

    public String move(String... params) throws ResponseException {
        if(params.length == 2 || params.length == 3){
            String start = params[0];
            String end = params[1];
            String promote;
            if(start.length() != 2 || end.length() != 2){
                return SET_TEXT_COLOR_RED + "moves need to be in format \"a4\"\n";
            }
            if(params.length == 3){
                promote = params[2];
                ChessPiece.PieceType piece = parsePiece(promote);
                if(piece == null){
                    return SET_TEXT_COLOR_RED + "Invalid promotion piece. (ex. queen)";
                }
                ws.move(auth, gameID, parseMove(start, end, piece));
                return RESET + String.format("moved pawn from %s to %s promoting to a %s", start, end, promote);
            }
            ws.move(auth, gameID, parseMove(start, end, null));
            return RESET;
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
                gameID = Integer.parseInt(params[0]);
                if(gameMap.containsKey(gameID)){
                    gameID = gameMap.get(gameID);
                }
            } catch (NumberFormatException e) {
                throw new ResponseException(400, SET_TEXT_COLOR_RED + "Invalid number: " + params[0]);
            }
            ws.connect(auth, gameID);
            state = State.IN_GAME;
            team = "white";
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
                ws.connect(auth, gameID);
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
            return RESET + String.format("You join game #%s as %s.\n", id, color);
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
        drawBoard(null);
    }

    private void drawBoard(Collection<ChessMove> possMoves){
        System.out.print("\n");
        drawHeader();
        drawSquares(possMoves);
        drawHeader();
    }

    private void drawSquares(Collection<ChessMove> possMoves){
        int color = 1;
        ChessPiece piece;
        ChessBoard board = game.getBoard();
        int rowStart = 8;
        int colStart = 1;
        int way = -1;
        if(team != null && team.equals("black")){
            rowStart = 1;
            colStart = 8;
            way = 1;
        }

        List<ChessPosition> endPos = new ArrayList<>();
        ChessPosition startPos = null;
        if(possMoves != null){
            for(ChessMove move: possMoves){
                endPos.add(move.getEndPosition());
                if(startPos == null) {
                    startPos = move.getStartPosition();
                }
            }
        }

        for(int row = rowStart; row <= 8 && row >= 1; row += way){
            System.out.print(BACKGROUND + " " + row + " ");
            for(int col = colStart; col <= 8 && col >= 1; col -= way){
                ChessPosition end = new ChessPosition(row, col);
                piece = board.getPiece(end);
                if(endPos.contains(end)){
                    System.out.print(SET_BG_COLOR_GREEN);
                } else if(end.equals(startPos)){
                    System.out.print(SET_BG_COLOR_YELLOW);
                }else {
                    if (color == 1) {
                        System.out.print(WHITE_SPACE);
                    } else {
                        System.out.print(BLACK_SPACE);
                    }
                }
                color *= -1;
                System.out.print(getPiece(piece));
            }
            color *= -1;
            System.out.println(BACKGROUND + " " + row + " " + RESET);
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

    private ChessMove parseMove(String start, String end, ChessPiece.PieceType promotion){
        return new ChessMove(parsePos(start), parsePos(end), promotion);
    }

    private ChessPosition parsePos(String pos){
        char row = pos.charAt(1);
        char col = pos.charAt(0);

        return new ChessPosition(row - '0', col - 'a' + 1);
    }

    private ChessPiece.PieceType parsePiece(String piece){
        return switch (piece.toLowerCase()){
            case "king" -> ChessPiece.PieceType.KING;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }
}
