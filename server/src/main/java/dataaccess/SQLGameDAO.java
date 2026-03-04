package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGameDAO implements GameDAO{

    @Override
    public int createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO game (gameName, game) VALUES (?, ?)";
        String json = new Gson().toJson(game.game());
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, game.gameName());
                ps.setString(2, json);
                ps.executeUpdate();

                var resultSet = ps.getGeneratedKeys();
                var gameID = 0;
                if (resultSet.next()) {
                    gameID = resultSet.getInt(1);
                }

                return gameID;
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    rs.next();
                    List<GameData> games = logGame(rs);
                    return games.getFirst();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    return logGame(rs);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String gameData = new Gson().toJson(game.game());
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameId=?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, gameData);
                ps.setInt(4, game.gameID());

                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM game";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    private List<GameData> logGame(ResultSet rs) throws SQLException {
        List<GameData> games = new ArrayList<>();
        while (rs.next()) {
            var id = rs.getInt("gameID");
            var whiteUsername = rs.getString("whiteUsername");
            var blackUsername = rs.getString("blackUsername");
            var gameName = rs.getString("gameName");
            var json = rs.getString("game");
            ChessGame game = new Gson().fromJson(json, ChessGame.class);
            GameData gameData = new GameData(id, whiteUsername, blackUsername, gameName, game);
            games.add(gameData);
        }

        return games;
    }
}
