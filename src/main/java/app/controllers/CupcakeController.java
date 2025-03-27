package app.controllers;

import app.entities.Cupcake;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class CupcakeController {


    public static void registerRoutes(Javalin app) {
        app.get("/choose", CupcakeController::showCupcakeForm);
        app.post("/add-cupcake", CupcakeController::handleCupcakeOrder);
    }

    private static void showCupcakeForm(Context ctx) {
        String user = ctx.sessionAttribute("user");

        if (user == null) {
            ctx.redirect("/login");
        } else {
            ctx.render("choose-cupcake.html");
        }
    }


    private static void handleCupcakeOrder(Context ctx) {
        String base = ctx.formParam("base");
        String topping = ctx.formParam("topping");


        ctx.render("cupcake-confirmation.html");
    }
}
