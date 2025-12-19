import Resources.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import io.javalin.http.Context;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("./toilets.json"));
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
                .get("/", ctx -> {
                    List<Toilet> toilets = featureCollection.features.stream()
                            .map(f -> new Toilet(
                                    f.properties.id,
                                    f.properties.name,
                                    f.geometry.coordinates.get(0),
                                    f.geometry.coordinates.get(1),
                                    f.properties.change_table_child,
                                    f.properties.fee
                            ))
                            .collect(Collectors.toList());
                    ctx.json(toilets);
                })
                .post("/", ctx -> runner.updateMap(ctx))
                .start(7070);
    }


    public void updateMap(Context ctx){
        System.out.println(ctx.body());
    }


}