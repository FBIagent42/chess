package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(2, user.email());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM auth WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    rs.next();
                    var name = rs.getString("username");
                    var password = rs.getString("password");
                    var email = rs.getString("email");
                    return new UserData(name, password, email);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM user";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("SQL Error", ex);
        }
    }
}
