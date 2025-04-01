package app.controllers;

import app.entities.Orders;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.http.Context;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    public static void depositToCustomerAccount(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        try {
            int userId = Integer.parseInt(ctx.formParam("user_id"));
            double amount = Double.parseDouble(ctx.formParam("amount"));

            String sql = "UPDATE users SET balance = balance + ? WHERE users_id = ?";

            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, amount);
                ps.setInt(2, userId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new DatabaseException("User not found.");
                }
            }

            ctx.status(200).result("Amount deposited successfully.");
        } catch (NumberFormatException | SQLException e) {
            ctx.status(400).result("Error processing request: " + e.getMessage());
        }
    }

    public static List<Orders> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY orders_date DESC";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("orders_id");
                int userId = rs.getInt("users_id");
                String orderDate = rs.getTimestamp("orders_date").toString();
                double totalPrice = rs.getDouble("total_price");
                boolean isCompleted = rs.getBoolean("is_completed");

                orders.add(new Orders(orderId, userId, orderDate, totalPrice, isCompleted));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving orders: " + e.getMessage());
        }

        return orders;
    }

    public static void showUsersOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> orders = getAllOrders(connectionPool);
        ctx.attribute("orders", orders);
        ctx.render("/admin/orders.html");
    }
}
