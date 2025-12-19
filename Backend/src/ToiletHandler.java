import Resources.Toilet;
import io.javalin.http.Context;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import Resources.*;

public class ToiletHandler {
    private List<Toilet> allToilets;
    private FeatureCollection featureCollection;

    public ToiletHandler() throws FileNotFoundException {
        Gson gson = new Gson();
        this.allToilets = new ArrayList<>();
        JsonReader reader = new JsonReader(new FileReader("./toilets.json"));
        this.featureCollection = gson.fromJson(reader, FeatureCollection.class);
    }


    public void getAllToilets(Context ctx) {
        List<Toilet> toilets = featureCollection.features.stream()
                .map(f ->

                        new Toilet(
                        f.properties.id,
                        f.properties.name,
                        f.geometry.coordinates.get(0),
                        f.geometry.coordinates.get(1),
                        f.properties.change_table_child,
                        f.properties.fee
                )

                )
                .collect(Collectors.toList());
        Map<String, List<String>> queryList = ctx.queryParamMap();
        if(!queryList.equals("{}")){
            filterToilets(ctx, queryList, toilets);
        }
        ctx.json(toilets);
    }

    public List<Toilet> filterToilets(Context ctx, Map<String, List<String>> filters, List<Toilet> toilets) {
        String table = ctx.queryParam("change_table_child");
        if(table != null) {
            toilets.removeIf(t -> !String.valueOf(t.getChange_table_child()).equals(table));
        } else{
            System.out.println("No filter");
        }
        return toilets;
    }

    public void addReview(Context ctx){

    }

    public void proximitySearch(Context ctx) {

    }
}
