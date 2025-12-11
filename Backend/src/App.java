import Resources.FeatureCollection;
import Resources.Properties;
import Resources.Toilet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.http.Context;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("./toilets.json"));
        //Properties properties = gson.fromJson(reader, Properties.class);
        FeatureCollection featureCollection = gson.fromJson(reader, FeatureCollection.class);

        App runner = new App();
        Javalin app = Javalin.create(config -> {
                    config.jsonMapper(new JavalinGson());
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:63342");
                });
            });
        })
                .get("/", ctx ->
                        ctx.json(featureCollection))
                .post("/", ctx -> runner.updateMap(ctx))
                .start(7070);
    }


    public void updateMap(Context ctx){
        System.out.println(ctx.body());
    }


}