package service;

import chess.ChessGame;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;

import java.util.Collection;

public class GameService implements Service{

    private static int nextGameID = 0;

    public Collection<GameData> listGames(String authToken){
        varifyAuth(authToken);
        return gameDoa.listGames();
    }
    public int createGame(CreateGameRequest createGameRequest){
        String name = createGameRequest.gameName();

        varifyAuth(createGameRequest.authToken());

        GameData game = new GameData(nextGameID, null, null, name, new ChessGame());
        nextGameID++;
        gameDoa.createGame(game);

        return nextGameID--;
    }
    public void joinGame(JoinGameRequest joinGameRequest){
        String color = joinGameRequest.playerColor();
        String username = authDOA.getAuth(joinGameRequest.authToken()).username();

        varifyAuth(joinGameRequest.authToken());

        GameData game = gameDoa.getGame(joinGameRequest.gameID());

        if(game == null){
            //throw(NoGameException());
        }
        if((color.equals("BLACK") && game.blackUsername() != null)
                || (color.equals("WHITE") && game.whiteUsername() != null)){
            //throw(ColorTakenException());
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
