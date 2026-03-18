package client;

import chess.ChessGame;
import client.main.ResponseException;
import client.main.ServerFacade;
import dataaccess.DataAccessException;
import model.GameData;
import model.requests.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.servieimplimentation.ClearService;



public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static String serverUrl = "http://localhost:8080";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        new ClearService().clear();
    }

    @Test
    public void registerPositive() throws ResponseException {
        var result = facade.register(new RegisterRequest("player1", "password", "email"));
        Assertions.assertEquals("player1", result.username());
        Assertions.assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void registerNegative() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));

        Assertions.assertThrows(ResponseException.class,
                () -> facade.register(new RegisterRequest("player1", "newPass", "newEmail")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.register(new RegisterRequest(null, "newPass", "newEmail")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.register(new RegisterRequest("player2", null, "newEmail")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.register(new RegisterRequest("player2", "newPass", null)));
    }

    @Test
    public void loginPositive() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));

        Assertions.assertEquals("player1", result.username());
        Assertions.assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void loginNegative() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));

        Assertions.assertThrows(ResponseException.class,
                () -> facade.login(new LoginRequest("player2", "password")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.login(new LoginRequest("player1", "newPass")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.login(new LoginRequest(null, "password")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.login(new LoginRequest("player1", null)));
    }

    @Test
    public void logoutPositive() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));
        facade.logout(new LogoutRequest(result.authToken()));

        Assertions.assertThrows(ResponseException.class,
                () -> facade.listGames(new ListGamesRequest(result.authToken())));
    }

    @Test
    public void logoutNegative() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        facade.login(new LoginRequest("player1", "password"));

        Assertions.assertThrows(ResponseException.class,
                () -> facade.logout(new LogoutRequest("notAnAuthToken")));
    }

    @Test
    public void listPositive() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));
        facade.createGame(new CreateGameRequest("game1", result.authToken()));
        facade.createGame(new CreateGameRequest("game2", result.authToken()));

        var listResult = facade.listGames(new ListGamesRequest(result.authToken()));

        ChessGame newGame = new ChessGame();
        newGame.getBoard().resetBoard();

        Assertions.assertTrue(listResult.games().contains(new GameData(1, null, null, "game1", newGame)));
        Assertions.assertTrue(listResult.games().contains(new GameData(2, null, null, "game2", newGame)));
    }

    @Test
    public void listNegative() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        facade.login(new LoginRequest("player1", "password"));

        Assertions.assertThrows(ResponseException.class,
                () -> facade.listGames(new ListGamesRequest("notAnAuthToken")));
    }

    @Test
    public void createPositive() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));
        var createResult = facade.createGame(new CreateGameRequest("game1", result.authToken()));
        var listResult = facade.listGames(new ListGamesRequest(result.authToken()));

        ChessGame newGame = new ChessGame();
        newGame.getBoard().resetBoard();

        Assertions.assertEquals(1, createResult.gameID());
        Assertions.assertTrue(listResult.games().contains(new GameData(1, null, null, "game1", newGame)));
    }

    @Test
    public void createNegative() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));

        Assertions.assertThrows(ResponseException.class,
                () ->facade.createGame(new CreateGameRequest("game1", "notAnAuthToken")));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.createGame(new CreateGameRequest(null, result.authToken())));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.createGame(new CreateGameRequest("game1", null)));
    }

    @Test
    public void joinPositive() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));
        var createResult = facade.createGame(new CreateGameRequest("game1", result.authToken()));
        facade.joinGame(new JoinGameRequest(result.authToken(), "white", createResult.gameID()));
        facade.joinGame(new JoinGameRequest(result.authToken(), "black", createResult.gameID()));
        var listResult = facade.listGames(new ListGamesRequest(result.authToken()));


        ChessGame newGame = new ChessGame();
        newGame.getBoard().resetBoard();

        Assertions.assertTrue(listResult.games().contains(new GameData(createResult.gameID(), "player1", "player1", "game1", newGame)));
    }

    @Test
    public void joinNegative() throws ResponseException {
        facade.register(new RegisterRequest("player1", "password", "email"));
        var result = facade.login(new LoginRequest("player1", "password"));
        var createResult = facade.createGame(new CreateGameRequest("game1", result.authToken()));
        facade.joinGame(new JoinGameRequest(result.authToken(), "white", createResult.gameID()));


        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(new JoinGameRequest(result.authToken(), "white", createResult.gameID())));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(new JoinGameRequest("notAnAuthToken", "white", createResult.gameID())));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(new JoinGameRequest(result.authToken(), "white", 100)));
    }
}
