package app.controllers;

import app.entities.Cupcake;
import app.entities.Bottom;
import app.entities.Top;
import app.exceptions.DatabaseException;
import app.persistence.CupcakeMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.http.Context;

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
            int userId = (int) ctx.sessionAttribute("userId"); // Assuming user is logged in and userId is stored in session
            int orderId = OrderMapper.getActiveOrderId(userId, connectionPool); // Get or create an active order

            // Insert the cupcake into the order with the specified quantity
            OrderMapper.addCupcakeToOrder(orderId, cupcake, quantity, connectionPool); // Add the cupcake with quantity to order

            // Store cupcake in context for confirmation
            ctx.attribute("cupcake", cupcake);
            ctx.attribute("quantity", quantity);


        } catch (DatabaseException e) {
            ctx.attribute("message", "Error processing cupcake order: " + e.getMessage());
            ctx.render("error.html");
        }
    }
}
