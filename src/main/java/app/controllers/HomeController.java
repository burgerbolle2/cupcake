package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.http.Context;

public class HomeController {
    private static void handleLogin(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("user_id", user.getUserId());
            ctx.sessionAttribute("user_role", user.getRole());  // Store role in session

            if ("admin".equals(user.getRole())) {
                ctx.redirect("/admin");  // Redirect admins to admin panel
            } else {
                ctx.redirect("/homepage");
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("team10/login.html");
        }
    }
    private static void handleCreateUser(Context ctx, ConnectionPool connectionPool) {
        // Retrieve user information from the form
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String role = "user"; // Standard role

        try {
            // Create the new user in the database
            UserMapper.createUser(email, password, connectionPool);
            ctx.attribute("message", "User created successfully!");
            ctx.redirect("/login"); // Redirect to login page after successful user creation
        } catch (DatabaseException e) {
            // If the email is already in use, display an error message
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Email already in use")) {
                ctx.attribute("message", "This email is already in use. Please login or use another email.");
            } else {
                ctx.attribute("message", "Error creating user: " + e.getMessage());
            }
            // Stay on the create-user page if there is an error
            ctx.render("team10/create-user.html");
        }
    }
}

