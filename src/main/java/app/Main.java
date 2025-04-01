package app;

import app.controllers.CupcakeController;
import app.controllers.HomeController;
import app.entities.Bottom;
import app.entities.Top;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.config.ThymeleafConfig;

import java.util.List;
import java.util.logging.Logger;


public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";

    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    private static final HomeController homeController = new HomeController(connectionPool);

    public static void main(String[] args) throws DatabaseException {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "public";
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });

            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        List<Top> tops = CupcakeMapper.getAllTops(connectionPool);
        List<Bottom> bottoms = CupcakeMapper.getAllBottoms(connectionPool);
        System.out.println(tops);
        System.out.println(bottoms);

        app.get("/", ctx -> homeController.home(ctx));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> homeController.handleLogin(ctx, connectionPool));
        app.get("/create-user", ctx -> ctx.render("/create-user.html"));
        app.post("/create-user", ctx -> homeController.handleCreateUser(ctx, connectionPool));
        app.get("/shop", ctx -> CupcakeController.showShopPage(ctx, connectionPool));
        app.post("/add-to-order", ctx -> CupcakeController.handleCupcakeChoice(ctx, connectionPool));
        app.post("/remove-from-order", ctx -> OrderMapper.removeCupcakeFromOrder(ctx, connectionPool));
        app.get("/checkout", ctx -> CupcakeController.showCheckoutPage(ctx, connectionPool));
        app.get("/payment", ctx -> HomeController.showPaymentPage(ctx, connectionPool));
        app.post("/complete-order", ctx -> OrderMapper.handleCheckout(ctx, connectionPool));

        //logout
        app.get("/logout", ctx -> homeController.home(ctx));
    }
}
