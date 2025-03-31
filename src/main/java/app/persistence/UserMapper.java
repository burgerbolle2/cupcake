package app.persistence;


import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email=? and password=?"; // Assuming 'email' is the username

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role"); // Retrieve the role from the database
                int id = rs.getInt("users_id");
                double balance = rs.getDouble("balance");
                // Return a User object with the role
                return new User(id, email, password, role, balance);
            } else {
                throw new DatabaseException("Error in login. Try again"); // Incorrect password
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB error", e.getMessage());
        }
    }


    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, password, role,balance) values (?,?,?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, "user");
            ps.setDouble(4, 100);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Error when creating user");
            }
        } catch (SQLException e) {
            String msg = "An error occured try again";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Email already in use, use another email";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }
    public static double getUserBalance(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT balance FROM users WHERE users_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                throw new DatabaseException("User not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving balance: " + e.getMessage());
        }
    }

    public static String getUserEmail(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT email FROM users WHERE users_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1,userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            }else {
                throw new DatabaseException("User not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving email: " + e.getMessage());
        }
    }

}
