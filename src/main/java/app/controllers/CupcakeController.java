package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.CupcakeMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void handleCupcakeChoice(Context ctx, ConnectionPool connectionPool) {
        try {
            // Get selected top and bottom IDs from form
            int bottomId = Integer.parseInt(ctx.formParam("bottom"));
            int topId = Integer.parseInt(ctx.formParam("top"));
            int quantity = Integer.parseInt(ctx.formParam("quantity"));

            // Fetch full Bottom and Top objects from the database
            Bottom bottom = CupcakeMapper.getBottomById(bottomId, connectionPool);
            Top top = CupcakeMapper.getTopById(topId, connectionPool);

            if (bottom == null || top == null || quantity <= 0) {
                ctx.attribute("message", "Invalid cupcake selection or quantity.");
                ctx.render("error.html");
                return;
            }

            // Create Cupcake (without ID, because DB will generate it)
            Cupcake cupcake = new Cupcake(bottom, top);

            // Save cupcake to database and get the generated ID
            cupcake = CupcakeMapper.insertCupcake(cupcake, connectionPool);

            // Add the cupcake and its quantity to the user's order (order_line)
            int userId = (int) ctx.sessionAttribute("users_id"); // Assuming user is logged in and userId is stored in session
            int orderId = OrderMapper.getLatestOrderId(userId, connectionPool);

            // Insert the cupcake into the order with the specified quantity
            OrderMapper.addCupcakeToOrder(orderId, cupcake, quantity, connectionPool); // Add the cupcake with quantity to order

            // Store cupcake in context for confirmation
            ctx.attribute("cupcake", cupcake);
            ctx.attribute("quantity", quantity);

            ctx.redirect("/shop");


        } catch (DatabaseException e) {
            ctx.attribute("message", "Error processing cupcake order: " + e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void showCheckoutPage(Context ctx, ConnectionPool connectionPool) {
        try {
            Integer userId = ctx.sessionAttribute("users_id");

            if (userId == null) {
                ctx.redirect("/login");
                return;
            }
            // Fetch cupcake options
            List<Top> tops = CupcakeMapper.getAllTops(connectionPool);
            List<Bottom> bottoms = CupcakeMapper.getAllBottoms(connectionPool);
            List<OrderLineDTO> orderLinesDTO = OrderMapper.getOrderLinesIfNotCompleted(userId, connectionPool);

            // If orderLines is empty, pass a custom message or flag
            boolean orderIsEmpty = orderLinesDTO.isEmpty();

            // Store the data in the context
            ctx.attribute("tops", tops);
            ctx.attribute("bottoms", bottoms);
            ctx.attribute("orderLinesDTO", orderLinesDTO);
            ctx.attribute("orderIsEmpty", orderIsEmpty);

            // Render the shop page using Thymeleaf
            ctx.render("checkout.html");
        } catch (DatabaseException e) {
            // Log the error and display an error page
            e.printStackTrace();
            ctx.attribute("message", "Error loading shop: " + e.getMessage());
            ctx.render("error.html");
        }
    }

    public static void showShopPage(Context ctx, ConnectionPool connectionPool) {
        try {
            // Check if the user is logged in
            Integer userId = ctx.sessionAttribute("users_id");

            if (userId == null) {
                // Redirect to login page if the user is not logged in
                ctx.redirect("/login");
                return;
            }

            // Fetch cupcake options
            List<Top> tops = CupcakeMapper.getAllTops(connectionPool);
            List<Bottom> bottoms = CupcakeMapper.getAllBottoms(connectionPool);
            List<OrderLineDTO> orderLinesDTO = OrderMapper.getOrderLinesIfNotCompleted(userId, connectionPool);

            // If orderLines is empty, pass a custom message or flag
            boolean orderIsEmpty = orderLinesDTO.isEmpty();

            // Store the data in the context
            ctx.attribute("tops", tops);
            ctx.attribute("bottoms", bottoms);
            ctx.attribute("orderLinesDTO", orderLinesDTO);
            ctx.attribute("orderIsEmpty", orderIsEmpty);

            // Render the shop page using Thymeleaf
            ctx.render("shop.html");
        } catch (DatabaseException e) {
            // Log the error and display an error page
            e.printStackTrace();
            ctx.attribute("message", "Error loading shop: " + e.getMessage());
            ctx.render("error.html");
        }
    }
}
