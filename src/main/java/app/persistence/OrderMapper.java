package app.persistence;

import app.entities.Cupcake;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {
    public static void addCupcakeToOrder(int orderId, Cupcake cupcake, int quantity, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO order_line (orders_id, cupcake_id, quantity, line_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, cupcake.getCupcakeId());
            ps.setInt(3, quantity);  // Set the quantity
            ps.setDouble(4, cupcake.getPrice() * quantity);  // Multiply the price by the quantity

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add cupcake to order: " + e.getMessage());
        }
    }

    public static int getActiveOrderId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        // Query to find if the user has an active (non-closed) order
        String sql = "SELECT orders_id FROM orders WHERE users_id = ? AND status = 'active' LIMIT 1";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("orders_id");
            } else {
                return -1; // No active order, need to create a new one
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
    }


    public static int createOrder(int userId, double totalPrice, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (users_id, order_date, total_price) VALUES (?, NOW(), ?) RETURNING orders_id";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDouble(2, totalPrice);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("orders_id");  // Return the generated orders_id
            } else {
                throw new DatabaseException("Failed to create order.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
    }

}
