package service;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.resulsts.CreateGameResult;
import service.resulsts.ListGamesResult;
import service.serviceExceptions.ColorTakenException;
import service.serviceExceptions.NoGameException;
import service.serviceExceptions.UnauthorizedException;

import java.util.Collection;
import java.util.List;

public class GameServiceTests implements BaseTests {

    @AfterEach
    public void clearAll(){
        gameDOA.clear();
        userDOA.clear();
        authDOA.clear();
    }

    @Test
    public void positiveListGames() {
        String authToken = "Test";
        addAuth(authToken, "Test");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        GameData game1 = new GameData(1, null, null, "test2ElectricBoogalo", new ChessGame());
        GameData game2 = new GameData(1234, "Corbin", "Bill", "Test", new ChessGame());
        gameDOA.createGame(game1);
        gameDOA.createGame(game2);

        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);

        Collection<GameData> testGames = List.of(game1, game2);

        Assertions.assertEquals(
                List.copyOf(listGamesResult.games()), List.copyOf(testGames));
    }

    @Test
    public void negativeListGames() {
        String authToken = "Wrong";
        addAuth("Right", "Test");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.listGames(listGamesRequest));
    }

    @Test
    public void positiveCreateGame() {
        String gameName = "test2ElectricBoogalo";
        String authToken = "Test";
        addAuth(authToken, "Test");
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName, authToken);

        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        GameData game = gameDOA.getGame(createGameResult.gameID());

        Assertions.assertEquals(game.gameID(), createGameResult.gameID());
        Assertions.assertEquals(gameName, game.gameName());
    }

    @Test
    public void negativeCreateGame() {
        String gameName = "Im sad :(";
        String authToken = "Wrong";
        addAuth("Right", "Test");
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName, authToken);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.createGame(createGameRequest));
    }

    @Test
    public void positiveWhiteJoinGame() {
        String color = "WHITE";
        String name = "Corbin";
        String authToken = "Test";
        int gameID = 8;
        addGame(gameID, "Test", new ChessGame());
        addAuth(authToken, name);

        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, color, gameID);

        gameService.joinGame(joinGameRequest);

        GameData game = gameDOA.getGame(gameID);

        Assertions.assertEquals(name, game.whiteUsername());
    }

    @Test
    public void positiveBlackJoinGame() {
        String color = "BLACK";
        String name = "Bill";
        String authToken = "Test";
        int gameID = 8;
        addGame(gameID, "Test", new ChessGame());
        addAuth(authToken, name);

        JoinGameRequest joinGameRequest = new JoinGameRequest("Test", color, gameID);

        gameService.joinGame(joinGameRequest);

        GameData game = gameDOA.getGame(gameID);

        Assertions.assertEquals(name, game.blackUsername());
    }

    @Test
    public void negativeJoinGameUnauthorized() {
        String color = "WHITE";
        int gameID = 1234;
        String authToken = "Wrong";
        addAuth("Right", "Test");
        addGame(gameID, "Test", new ChessGame());

        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, color, gameID);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame(joinGameRequest));
    }

    @Test
    public void negativeJoinGameNoGame(){
        String color = "WHITE";
        int gameID = 42;
        String authToken = "Test";
        addAuth(authToken, "Test");
        addGame(1, "Test", new ChessGame());

        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, color, gameID);

        Assertions.assertThrows(NoGameException.class,
                () -> gameService.joinGame(joinGameRequest));
    }

    @Test
    public void negativeWhiteJoinGame(){
        String color = "WHITE";
        int gameID = 1234;
        String authToken = "Test";
        addAuth(authToken, "Test");
        gameDOA.createGame(new GameData(gameID, "Full", null, "Test", new ChessGame()));
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, color, gameID);

        Assertions.assertThrows(ColorTakenException.class,
                () -> gameService.joinGame(joinGameRequest));
    }

    @Test
    public void negativeBlackJoinGame(){
        String color = "BLACK";
        int gameID = 1234;
        String authToken = "Test";
        addAuth(authToken, "Test");
        gameDOA.createGame(new GameData(gameID, null, "Full", "Test", new ChessGame()));
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, color, gameID);

        Assertions.assertThrows(ColorTakenException.class,
                () -> gameService.joinGame(joinGameRequest));
    }

}
