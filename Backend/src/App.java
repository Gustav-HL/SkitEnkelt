import Resources.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import io.javalin.http.Context;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    static Gson gson = new Gson();
    static FeatureCollection featureCollection;
    static Reviews reviewsCollection ;

    public static void main(String[] args) throws FileNotFoundException {
        JsonReader toiletReader = new JsonReader(new FileReader("./toilets.json"));
        JsonReader reviewReader = new JsonReader(new FileReader("./reviews.json"));
        featureCollection = gson.fromJson(toiletReader, FeatureCollection.class);
        reviewsCollection = gson.fromJson(reviewReader, Reviews.class);

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
                            .map(f -> {
                                try {
                                    return new Toilet(
                                            f.properties.id,
                                            f.properties.name,
                                            f.geometry.coordinates.get(0),
                                            f.geometry.coordinates.get(1),
                                            f.properties.change_table_child,
                                            f.properties.fee,
                                            f.properties.report_phone,
                                            f.properties.report_url,
                                            f.properties.open_hours,
                                            f.properties.open_season,
                                            f.properties.wc,
                                            f.properties.accessability,
                                            getReviewsForSelectedToilet(f.properties.id)
                                    );
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());
                    ctx.json(toilets);
                })
                .post("/", ctx -> runner.updateMap(ctx))
                .start(7070);
    }


    public void updateMap(Context ctx){
        System.out.println(ctx.body());
    }

    public static List<Review> getReviewsForSelectedToilet(int toiletId) throws FileNotFoundException {
        List<Review> result = new ArrayList<>();

        for(Review review : reviewsCollection.getReviews()){
            if(review.getToiletId() == toiletId){
                result.add(review);
            }
        }
        return result;
    }


}