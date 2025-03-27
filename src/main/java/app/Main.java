package app;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.config.ThymeleafConfig;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";                     // URL path
                staticFiles.directory = "public";                 // folder in resources/
                staticFiles.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });

            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // ✅ Show homepage.html when going to /homepage
        app.get("/homepage", ctx -> ctx.render("homepage.html"));

        // ✅ Redirect root (/) to homepage
        app.get("/", ctx -> ctx.redirect("/homepage"));

        // ✅ Login page
        app.get("/login", ctx -> ctx.render("login.html"));
    }
}
