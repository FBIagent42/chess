package service.servieimplimentation;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.ListGamesRequest;
import model.resulsts.CreateGameResult;
import model.resulsts.ListGamesResult;
import service.serviceexceptions.*;

public class GameService extends Service{
    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        verifyAuth(listGamesRequest.authToken());
        return new ListGamesResult(gameDAO.listGames());
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String name = createGameRequest.gameName();

        verifyAuth(createGameRequest.authToken());

        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();
        GameData gameData = new GameData(0, null, null, name, game);
        int gameID = gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        verifyAuth(joinGameRequest.authToken());

        String color = joinGameRequest.playerColor();
        String username = authDAO.getAuth(joinGameRequest.authToken()).username();

        GameData game = gameDAO.getGame(joinGameRequest.gameID());

        if(game == null){
            throw(new NoGameException());
        }
        if((color.equalsIgnoreCase("black") && game.blackUsername() != null)
                || (color.equalsIgnoreCase("white") && game.whiteUsername() != null)){
            throw(new ColorTakenException());
        }

        if(color.equalsIgnoreCase("white")){
            gameDAO.updateGame(new GameData(game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()));
        }
        if(color.equalsIgnoreCase("black")){
            gameDAO.updateGame(new GameData(game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()));
        }
    }
}
