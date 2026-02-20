package service.servieimplimentation;

import chess.ChessGame;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.resulsts.CreateGameResult;
import service.resulsts.ListGamesResult;
import service.serviceexceptions.*;

public class GameService implements Service{

    private static int nextGameID = 1;

    public ListGamesResult listGames(ListGamesRequest listGamesRequest){
        varifyAuth(listGamesRequest.authToken());
        return new ListGamesResult(GAME_DAO.listGames());
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest){
        String name = createGameRequest.gameName();

        varifyAuth(createGameRequest.authToken());

        GameData game = new GameData(nextGameID, null, null, name, new ChessGame());
        nextGameID++;
        GAME_DAO.createGame(game);

        return new CreateGameResult(nextGameID - 1);
    }
    public void joinGame(JoinGameRequest joinGameRequest){
        varifyAuth(joinGameRequest.authToken());

        String color = joinGameRequest.playerColor();
        String username = AUTH_DAO.getAuth(joinGameRequest.authToken()).username();

        GameData game = GAME_DAO.getGame(joinGameRequest.gameID());

        if(game == null){
            throw(new NoGameException());
        }
        if((color.equals("BLACK") && game.blackUsername() != null)
                || (color.equals("WHITE") && game.whiteUsername() != null)){
            throw(new ColorTakenException());
        }

        if(color.equals("WHITE")){
            GAME_DAO.updateGame(new GameData(game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()));
        }
        if(color.equals("BLACK")){
            GAME_DAO.updateGame(new GameData(game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()));
        }
    }
}
