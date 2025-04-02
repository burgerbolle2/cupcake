package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.http.Context;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {


    public static List<Orders> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE is_completed = TRUE ORDER BY orders_date DESC";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("orders_id");
                int userId = rs.getInt("users_id");
                String orderDate = rs.getTimestamp("orders_date").toString();
                double totalPrice = rs.getDouble("total_price");

                orders.add(new Orders(orderId, userId, orderDate, totalPrice, true));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error retrieving completed orders: " + e.getMessage());
        }
        return orders;
    }

    public static List<OrdersDTO> getCompletedOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<OrdersDTO> orderDTOs = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM orders o " +
                "JOIN users u ON o.users_id = u.users_id " +
                "WHERE o.is_completed = TRUE";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("orders_id");
                int userId = rs.getInt("users_id");
                String userEmail = rs.getString("email");
                String orderDate = rs.getString("orders_date");
                double totalPrice = rs.getDouble("total_price");


                // Create DTO instead of Order entity
                OrdersDTO orderDTO = new OrdersDTO(orderId, userId, userEmail, orderDate, totalPrice, true);
                List<OrderLineDTO> orderLines = OrderMapper.getOrderLinesDTOByOrderId(orderId, connectionPool);
                orderDTO.setOrderLines(orderLines);
                orderDTOs.add(orderDTO);


            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching completed orders", e.getMessage());
        }

        return orderDTOs;
    }


    public static void showUsersOrders(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        List<OrdersDTO> orders = getCompletedOrders(connectionPool);
        ctx.attribute("orders", orders);
        ctx.render("/admin/customer-orders.html");
    }

    public static void showAdminPage(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        String email = ctx.sessionAttribute("email");
        ctx.attribute("email", email);
        ctx.render("/admin/admin-homepage.html");
    }


    public static void showDepositPage(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = UserMapper.getAllUsers(connectionPool);
        ctx.attribute("users", users);
        ctx.render("admin/admin-insert-balance.html");
    }


    public static void handleInsertBalance(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Integer userId = Integer.valueOf(ctx.formParam("userId"));
        double amount = Double.parseDouble(ctx.formParam("amount"));

        UserMapper.depositMoney(userId, amount, connectionPool);

        ctx.sessionAttribute("message", "Balance added successfully!");
        ctx.redirect("/admin");

    }

    public static void removeOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Integer orderId = Integer.valueOf(ctx.formParam("orderId"));
        OrderMapper.removeOrder(orderId, connectionPool);
        ctx.attribute("message", "Order removed successfully!");
        ctx.redirect("/admin-orders");
    }

}
