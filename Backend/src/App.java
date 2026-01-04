import Resources.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        ToiletHandler toiletHandler = new ToiletHandler();

        App runner = new App();
        Javalin app = Javalin.create(config -> {
                    config.jsonMapper(new JavalinGson());
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        })
                .get("/toilets", ctx -> {
                    toiletHandler.getAllToilets(ctx);
                })
                .post("/", ctx -> toiletHandler.addReview(ctx))
                .start(7071);
    }


}