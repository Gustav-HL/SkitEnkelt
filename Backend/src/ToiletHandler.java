import Resources.Toilet;
import io.javalin.http.Context;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import Resources.*;

public class ToiletHandler {
    private FeatureCollection featureCollection;
    private final String toiletsUrl = "https://ckan-malmo.dataplatform.se/dataset/82b9290d-bd82-4611-ae28-161f95c71339/resource/81b70be0-1860-467f-bfcc-5bf70d094dd0/download/offentliga_toaletter.json";

    public ToiletHandler() {
        getToiletsAnew();
    }

    public void getToiletsAnew() {
        JsonReader reader = new JsonReader(new StringReader(callToiletsAPI()));
        Gson gson = new Gson();
        this.featureCollection = gson.fromJson(reader, FeatureCollection.class);
    }


    public String callToiletsAPI() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(toiletsUrl)).header("Accept", "application/json").GET().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void getAllToilets(Context ctx) {
        getToiletsAnew();
        List<Toilet> toilets = featureCollection.features.stream()
                .map(f ->

                        new Toilet(
                                f.properties.id,
                                f.properties.name,
                                f.geometry.coordinates.get(0),
                                f.geometry.coordinates.get(1),
                                f.properties.change_table_child,
                                f.properties.fee,
                                f.properties.wc
                        )

                )
                .collect(Collectors.toList());
        //queryList ämnar effektivisera till hur filter hanteras, görs för nuvarande endast manuellt
        Map<String, List<String>> queryList = ctx.queryParamMap();
        if (!queryList.equals("{}")) {
            toilets = filterToilets(ctx, queryList, toilets);
        }
        ctx.json(toilets);
    }

    //Inparameter filters gör i nuvarande inget, berör om filter-hanteringen ska effektivseras eller inte
    public List<Toilet> filterToilets(Context ctx, Map<String, List<String>> filters, List<Toilet> toilets) {
        String tableChild = ctx.queryParam("change_table_child");
        String feeExist = ctx.queryParam("fee");
        String nbrWcs = ctx.queryParam("wc");


        //Nuvarande existerande filerhanteringar: tableChild, fee, wcs
        if (tableChild != null) {
            toilets.removeIf(t -> t.getChange_table_child() < 1);
        }
        if (feeExist != null) {
            toilets.removeIf(t -> t.getFee() != null && !t.getFee().isEmpty());
        }
        if (nbrWcs != null && !nbrWcs.isBlank()) {
            int minWc = Integer.parseInt(nbrWcs);
            toilets.removeIf(t -> t.getNbrWcs() < minWc);
        }
        return toilets;
    }

    public void addReview(Context ctx) {
    }

    public void proximitySearch(Context ctx) {
        String latParam = ctx.queryParam("lat");
        String lonParam = ctx.queryParam("lon");
        String rangeParam = ctx.queryParam("range"); //avstånd i meter

        if (latParam == null || lonParam == null || rangeParam == null) {
            ctx.status(400).result("lat, lon och range krävs");
            return;
        }

        double lat = Double.parseDouble(latParam);
        double lon = Double.parseDouble(lonParam);
        double range = Double.parseDouble(rangeParam);

        getToiletsAnew();
        List<Toilet> toilets = featureCollection.features.stream()
                .map(f -> new Toilet(
                        f.properties.id,
                        f.properties.name,
                        f.geometry.coordinates.get(0),
                        f.geometry.coordinates.get(1),
                        f.properties.change_table_child,
                        f.properties.fee,
                        f.properties.wc
                ))
                .filter(t -> {
                    double distance = calculateDistance(
                            lat,
                            lon,
                            t.getLat(),
                            t.getLng()
                    );
                    return distance <= range; //returnerar alla resultat som passar inom range.
                })
                .collect(Collectors.toList());

        ctx.json(toilets);

    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; //jordens diameter i meter
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
