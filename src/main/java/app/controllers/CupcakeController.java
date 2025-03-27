package app.controllers;

import app.entities.cupcake;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class CupcakeController {

    // Dummy priser
    private static final Map<String, Integer> basePrices = new HashMap<>();
    private static final Map<String, Integer> toppingPrices = new HashMap<>();

    static {
        basePrices.put("Chokolade", 20);
        basePrices.put("Vanilje", 18);
        basePrices.put("Red velvet", 22);

        toppingPrices.put("Jordbær", 12);
        toppingPrices.put("Chokoladefrosting", 15);
        toppingPrices.put("Karamel", 14);
    }

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

        int basePrice = basePrices.getOrDefault(base, 0);
        int toppingPrice = toppingPrices.getOrDefault(topping, 0);

        cupcake cupcake = new cupcake(base, topping, basePrice, toppingPrice);

        ctx.attribute("cupcake", cupcake);
        ctx.render("cupcake-confirmation.html");
    }
}
