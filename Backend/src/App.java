import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create()
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}