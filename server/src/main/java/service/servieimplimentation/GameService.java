package service.servieimplimentation;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.resulsts.CreateGameResult;
import service.resulsts.ListGamesResult;
import service.serviceexceptions.*;

public class GameService extends Service{
    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        verifyAuth(listGamesRequest.authToken());
        return new ListGamesResult(GAME_DAO.listGames());
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String name = createGameRequest.gameName();

        verifyAuth(createGameRequest.authToken());

        GameData game = new GameData(0, null, null, name, new ChessGame());
        int gameID = GAME_DAO.createGame(game);

        return new CreateGameResult(gameID);
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        verifyAuth(joinGameRequest.authToken());

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
