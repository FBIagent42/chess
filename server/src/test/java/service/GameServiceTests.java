package service;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.resulsts.CreateGameResult;
import service.resulsts.ListGamesResult;

import java.util.Collection;
import java.util.List;

public class GameServiceTests implements BaseTests{

    @Test
    public void positiveListGames(){
        String authToken = "Test";
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);

        GameData game1 = new GameData(1, null, null, "test2ElectricBoogalo", new ChessGame());
        GameData game2 = new GameData(1234, "Corbin", "Bill", "Test", new ChessGame());
        gameDOA.createGame(game1);

        ListGamesResult listGamesResult =  gameService.listGames(listGamesRequest);

        Collection<GameData> testGames = List.of(game1, game2);

        Assertions.assertEquals(
                List.copyOf(listGamesResult.games()), List.copyOf(testGames));
    }

    @Test
    public void positiveCreateGame(){
        String gameName = "test2ElectricBoogalo";
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName, "Test");

        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        GameData game = gameDOA.getGame(createGameResult.gameID());

        Assertions.assertEquals(game.gameID(), createGameResult.gameID());
        Assertions.assertEquals(gameName, game.gameName());
    }

    @Test
    public void positiveWhiteJoinGame(){
        String color = "White";
        int gameID = 1234;
        String name = "Corbin";
        JoinGameRequest joinGameRequest = new JoinGameRequest("Test", color, gameID);

        gameService.joinGame(joinGameRequest);

        GameData game = gameDOA.getGame(gameID);

        Assertions.assertEquals(name, game.whiteUsername());
    }

    @Test
    public void positiveBlackJoinGame(){
        String color = "Black";
        int gameID = 1234;
        String name = "Bill";
        JoinGameRequest joinGameRequest = new JoinGameRequest("Test", color, gameID);

        gameService.joinGame(joinGameRequest);

        GameData game = gameDOA.getGame(gameID);

        Assertions.assertEquals(name, game.blackUsername());
    }
}
