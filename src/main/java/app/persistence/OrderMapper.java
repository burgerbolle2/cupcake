package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public static List<OrderLineDTO> getOrderLinesDTO(int userId, ConnectionPool connectionPool) throws DatabaseException {
        List<OrderLineDTO> orderLineDTOs = new ArrayList<>();
        String sql = """
        SELECT ol.orders_id, ol.cupcake_id, ol.quantity, 
               (b.price + t.price) * ol.quantity AS line_price,
               b.name AS bottom_name, t.name AS top_name
        FROM order_line ol
        JOIN cupcake c ON ol.cupcake_id = c.cupcake_id
        JOIN bottom b ON c.bottom_id = b.bottom_id
        JOIN top t ON c.top_id = t.top_id
        JOIN orders o ON ol.orders_id = o.orders_id
        WHERE o.users_id = ?
    """;

        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("orders_id");
                int cupcakeId = rs.getInt("cupcake_id");
                int quantity = rs.getInt("quantity");
                double linePrice = rs.getDouble("line_price");
                String bottomName = rs.getString("bottom_name");
                String topName = rs.getString("top_name");

                // Map the data into an OrderLineDTO object
                OrderLineDTO orderLineDTO = new OrderLineDTO(orderId, cupcakeId, quantity, linePrice, bottomName, topName);
                orderLineDTOs.add(orderLineDTO);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving order lines: " + e.getMessage());
        }
        return orderLineDTOs;
    }


    public static List<OrderLine> getOrderLines(int userId, ConnectionPool connectionPool) throws DatabaseException {
        List<OrderLine> orderLines = new ArrayList<>();
        String sql = """
        SELECT ol.orders_id, ol.cupcake_id, ol.quantity, 
               (b.price + t.price) * ol.quantity AS line_price
        FROM order_line ol
        JOIN cupcake c ON ol.cupcake_id = c.cupcake_id
        JOIN bottom b ON c.bottom_id = b.bottom_id
        JOIN top t ON c.top_id = t.top_id
        JOIN orders o ON ol.orders_id = o.orders_id
        WHERE o.users_id = ?
    """;

        try (Connection conn = connectionPool.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("orders_id");
                int cupcakeId = rs.getInt("cupcake_id");
                int quantity = rs.getInt("quantity");
                double linePrice = rs.getDouble("line_price");

                orderLines.add(new OrderLine(orderId, cupcakeId, quantity, linePrice));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving order lines: " + e.getMessage());
        }
        return orderLines;
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
    public static int getLatestOrderId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String findOrderSql = "SELECT orders_id FROM orders WHERE users_id = ? ORDER BY orders_id DESC LIMIT 1";
        String createOrderSql = "INSERT INTO orders (users_id) VALUES (?) RETURNING orders_id";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement findPs = conn.prepareStatement(findOrderSql)) {

            findPs.setInt(1, userId);
            ResultSet rs = findPs.executeQuery();

            if (rs.next()) {
                return rs.getInt("orders_id"); // Return latest order
            } else {
                // No existing order found, create a new one
                try (PreparedStatement createPs = conn.prepareStatement(createOrderSql)) {
                    createPs.setInt(1, userId);
                    ResultSet createRs = createPs.executeQuery();
                    if (createRs.next()) {
                        return createRs.getInt("orders_id"); // Return new order ID
                    } else {
                        throw new DatabaseException("Failed to create new order.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error: " + e.getMessage());
        }
    }


}
