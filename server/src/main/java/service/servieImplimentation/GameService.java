package service.servieImplimentation;

import chess.ChessGame;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.resulsts.CreateGameResult;
import service.resulsts.ListGamesResult;
import service.serviceExceptions.*;

public class GameService implements Service{

    private static int nextGameID = 0;

    public ListGamesResult listGames(ListGamesRequest listGamesRequest){
        varifyAuth(listGamesRequest.authToken());
        return new ListGamesResult(gameDoa.listGames());
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest){
        String name = createGameRequest.gameName();

        varifyAuth(createGameRequest.authToken());

        GameData game = new GameData(nextGameID, null, null, name, new ChessGame());
        nextGameID++;
        gameDoa.createGame(game);

        return new CreateGameResult(nextGameID--);
    }
    public void joinGame(JoinGameRequest joinGameRequest){
        String color = joinGameRequest.playerColor();
        String username = authDOA.getAuth(joinGameRequest.authToken()).username();

        varifyAuth(joinGameRequest.authToken());

        GameData game = gameDoa.getGame(joinGameRequest.gameID());

        if(game == null){
            throw(new NoGameException());
        }
        if((color.equals("BLACK") && game.blackUsername() != null)
                || (color.equals("WHITE") && game.whiteUsername() != null)){
            throw(new ColorTakenException());
        }

        if(color.equals("WHITE")){
            gameDoa.updateGame(new GameData(game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()));
        }
        if(color.equals("BLACK")){
            gameDoa.updateGame(new GameData(game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()));
        }
    }
}
