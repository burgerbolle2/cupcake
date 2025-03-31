package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import io.javalin.http.Context;

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

    public static void removeCupcakeFromOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        try {
            // Hent 'orderId' og 'cupcakeId' fra requesten
            int orderId = Integer.parseInt(ctx.formParam("orders_id"));
            int cupcakeId = Integer.parseInt(ctx.formParam("cupcake_id"));

            // Kald metoden til at fjerne cupcake fra ordren
            String sql = "DELETE FROM order_line WHERE orders_id = ? AND cupcake_id = ?";

            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.setInt(2, cupcakeId);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DatabaseException("No matching cupcake found in the order.");
                }
            } catch (SQLException e) {
                throw new DatabaseException("Failed to remove cupcake from order: " + e.getMessage());
            }

            // Efter succesfuld fjernelse, send et svar tilbage
            ctx.status(200).result("Cupcake removed successfully.");
        } catch (NumberFormatException | DatabaseException e) {
            // Håndter fejl og send et passende svar
            ctx.status(400).result("Failed to remove cupcake: " + e.getMessage());
        }
    }
    public static List<OrderLineDTO> getOrderLinesIfNotCompleted(int userId, ConnectionPool connectionPool) throws DatabaseException {
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
                WHERE o.users_id = ? AND o.is_completed = FALSE
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


    public static int getLatestOrderId(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String findOrderSql = "SELECT orders_id FROM orders WHERE users_id = ? AND is_completed = FALSE";
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

    public static void handleCheckout(Context ctx, ConnectionPool connectionPool) {
        try {
            int userId = (int) ctx.sessionAttribute("users_id");
            int orderId = OrderMapper.getLatestOrderId(userId, connectionPool);

            OrderMapper.completeOrder(userId, orderId, connectionPool);

            ctx.attribute("message", "Order completed successfully!");
            ctx.redirect("/confirmation"); // Redirect user to confirmation after checkout
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("error.html");
        }
    }

    public static double getTotalOrderPrice(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT SUM(line_price) AS total_price FROM order_line WHERE orders_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_price");
            } else {
                return 0.0; // Hvis der ikke er nogle orderlines
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving total order price: " + e.getMessage());
        }
    }


    public static void completeOrder(int userId, int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String getTotalPriceSql = "SELECT SUM(line_price) AS total_price FROM order_line WHERE orders_id = ?";
        String getBalanceSql = "SELECT balance FROM users WHERE users_id = ?";
        String updateBalanceSql = "UPDATE users SET balance = balance - ? WHERE users_id = ?";
        String updateOrderSql = "UPDATE orders SET total_price = ?, orders_date = NOW(), is_completed = TRUE WHERE orders_id = ?";

        try (Connection conn = connectionPool.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            double totalPrice;
            // Get total price from order lines
            try (PreparedStatement ps = conn.prepareStatement(getTotalPriceSql)) {
                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    totalPrice = rs.getDouble("total_price");
                } else {
                    throw new DatabaseException("Error calculating order total.");
                }
            }

            // Get user balance
            double balance;
            try (PreparedStatement ps = conn.prepareStatement(getBalanceSql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    balance = rs.getDouble("balance");
                } else {
                    throw new DatabaseException("User not found.");
                }
            }

            // Check if the user has enough balance
            if (balance < totalPrice) {
                throw new DatabaseException("Insufficient balance.");
            }

            // Deduct balance
            try (PreparedStatement ps = conn.prepareStatement(updateBalanceSql)) {
                ps.setDouble(1, totalPrice);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Update orders table with total price, order time, and mark as completed
            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                ps.setDouble(1, totalPrice);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            throw new DatabaseException("Error completing order: " + e.getMessage());
        }
    }

}
