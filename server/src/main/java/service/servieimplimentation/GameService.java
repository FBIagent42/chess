package service.servieimplimentation;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import model.GameData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.ListGamesRequest;
import model.resulsts.CreateGameResult;
import model.resulsts.ListGamesResult;
import service.serviceexceptions.*;

import java.util.Objects;

public class GameService extends Service{
    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        verifyAuth(listGamesRequest.authToken());
        return new ListGamesResult(gameDAO.listGames());
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String name = verifyAuth(createGameRequest.authToken());
        ChessGame game = new ChessGame();
        game.getBoard().resetBoard();
        GameData gameData = new GameData(0, null, null, name, game);
        int gameID = gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        String color = joinGameRequest.playerColor();
        String username = verifyAuth(joinGameRequest.authToken());

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

    public void makeMove(int gameID, ChessMove move) throws DataAccessException, InvalidMoveException {
        GameData data = gameDAO.getGame(gameID);
        ChessGame game = data.game();
        game.makeMove(move);
        gameDAO.updateGame(new GameData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName(), game));
    }

    public String getColor(int gameID, String username) throws DataAccessException {
        if(gameDAO.getGame(gameID) == null){
            throw new DataAccessException("No Game with that ID");
        }
        if(Objects.equals(gameDAO.getGame(gameID).whiteUsername(), username)){
            return "white";
        } else if (Objects.equals(gameDAO.getGame(gameID).blackUsername(), username)) {
            return "black";
        } else {
            return "an observer";
        }
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID).game();
    }

    public boolean gameOver(int gameID) throws DataAccessException {
        GameData data = gameDAO.getGame(gameID);
        ChessGame game =  data.game();
        if(game.isGameOver()){
            return false;
        }
        game.setGameOver(true);
        gameDAO.updateGame(new GameData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName(), game));
        return true;
    }

    public void leaveGame(int gameID, String color) throws DataAccessException {
        GameData data = gameDAO.getGame(gameID);
        if(Objects.equals(color, "white")){
            gameDAO.updateGame(new GameData(data.gameID(), null, data.blackUsername(), data.gameName(), data.game()));
        } else if(Objects.equals(color, "black")){
            gameDAO.updateGame(new GameData(data.gameID(), null, data.blackUsername(), data.gameName(), data.game()));
        }
    }
}
