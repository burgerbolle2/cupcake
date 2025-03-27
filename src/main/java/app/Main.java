package app;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.config.ThymeleafConfig;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "public";
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });

            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Hjemmeside
        app.get("/", ctx -> ctx.redirect("/homepage"));
        app.get("/homepage", ctx -> ctx.render("homepage.html"));

        // Login side (GET)
        app.get("/login", ctx -> ctx.render("login.html"));

        // Login form (POST)
        app.post("/login", ctx -> {
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");

            // Simpelt dummy-login — kan erstattes med database senere
            if ("user@example.com".equals(email) && "password".equals(password)) {
                ctx.sessionAttribute("user", email);
                ctx.redirect("/homepage");
            } else {
                ctx.attribute("message", "Invalid email or password.");
                ctx.render("login.html");
            }
        });

        // Logout
        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/homepage");
        });

        // Viser bestillingssiden kun hvis logget ind
        app.get("/choose", ctx -> {
            if (ctx.sessionAttribute("user") == null) {
                ctx.redirect("/login");
            } else {
                ctx.render("choose-cupcake.html");
            }
        });
    }
}

