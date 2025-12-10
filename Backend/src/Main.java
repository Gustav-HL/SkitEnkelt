import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
                    config.plugins.enableCors(cors -> {
                        cors.add(CorsPluginConfig::anyHost);
                    });
                })
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}