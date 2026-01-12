import Resources.Toilet;
import io.javalin.http.Context;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import Resources.*;

public class ToiletHandler {
    private FeatureCollection featureCollection;
    static Reviews reviewsCollection;
    private final String toiletsUrl = "https://ckan-malmo.dataplatform.se/dataset/82b9290d-bd82-4611-ae28-161f95c71339/resource/81b70be0-1860-467f-bfcc-5bf70d094dd0/download/offentliga_toaletter.json";
    private final String reviewsUrl = "reviews.json";
    private final String R = String.valueOf(6371000);
    Gson gson = new Gson();

    public ToiletHandler() throws FileNotFoundException {
        getToiletsAnew();
    }

    public void getToiletsAnew() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new StringReader(callToiletsAPI()));
        JsonReader reviewReader = new JsonReader(new FileReader("./reviews.json"));

        this.featureCollection = gson.fromJson(reader, FeatureCollection.class);
        reviewsCollection = gson.fromJson(reviewReader, Reviews.class);
    }


    public String callToiletsAPI() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(toiletsUrl))
                    .header("Accept", "application/json")
                    .GET().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String postReviewAPI(String reviewsUrl, String reviewJson) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(reviewsUrl))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reviewJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new RuntimeException("POST failed: " + status + " body=" + response.body());
            }
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);

        }
    }


    public void getAllToilets(Context ctx) throws FileNotFoundException {
        getToiletsAnew();
        String latParam = ctx.queryParam("lat");
        String lonParam = ctx.queryParam("lon");
        String rangeParam = ctx.queryParam("range");

        if (rangeParam == null) rangeParam = R;
        if (latParam == null) latParam = "55.605";
        if (lonParam == null) lonParam = "13.003";

        double lat = Double.parseDouble(latParam);
        double lon = Double.parseDouble(lonParam);
        double range = Double.parseDouble(rangeParam);

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
                                f.properties.wc,
                                getReviewsByToiletId(f.properties.id),
                                getAverageRatingByToiletId(f.properties.id),
                                f.properties.open_hours,
                                getShittyNess()
                        );
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(t -> {
                    double distance = calculateDistance(
                            lat,
                            lon,
                            t.getLat(),
                            t.getLng()
                    );
                    t.setDistance(distance);
                    return t;
                })
                .filter(t -> ctx.queryParam("change_table_child") == null || t.getChange_table_child() > 0)
                .filter(t -> ctx.queryParam("fee") == null || t.getFee().isBlank())
                .filter(t -> ctx.queryParam("wc") == null || t.getNbrWcs() >= Integer.parseInt(Objects.requireNonNull(ctx.queryParam("wc"))))
                .filter(t -> t.getDistance() <= range)
                .sorted(Comparator.comparingDouble(Toilet::getDistance))
                .collect(Collectors.toList());

        int numberOfShittersCloseBy = 0;
        int numbersOfChangingTables = 0;
        for (Toilet t : toilets) {
            numberOfShittersCloseBy += t.getNbrWcs();
            numbersOfChangingTables += t.getChange_table_child();

            System.out.println(
                    t.getName() + " → " + Math.round(t.getDistance()) + " meters"
            );
        }

        System.out.println("Number of toilets in range: " + numberOfShittersCloseBy);
        System.out.println("Number of changing tables in range: " + numbersOfChangingTables);

        ctx.json(toilets);
    }

    private String getShittyNess(int toiletId) {
    return String.valueOf(reviewsCollection.getAverageShittyness(toiletId));}

    public void addReview(Context ctx) throws FileNotFoundException {
        getToiletsAnew();

        Review incoming = ctx.bodyAsClass(Review.class);
        int maxId = 0;
        for (Review r : reviewsCollection.getReviews()) {
            if (r.getId() > maxId) maxId = r.getId();
        }
        incoming.setId(maxId + 1);

        reviewsCollection.getReviews().add(incoming);

        Gson gson = new Gson();
        String updated = gson.toJson(reviewsCollection);
        try {
            Files.writeString(
                    Path.of("./reviews.json"),
                    updated,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




        ctx.status(201).json(incoming);
    }


    public static List<Review> getReviewsByToiletId(int toiletId) throws FileNotFoundException {
        List<Review> result = new ArrayList<>();

        for(Review review : reviewsCollection.getReviews()){
            if(review.getToiletId() == toiletId){
                result.add(review);
            }
        }
        return result;
    }

    //HTTP requests kräver ctx och Toilet-objektet fetchar reviews med toiletId, så metoden övanpå var inkompatibel.
    //Därför behöver vi en till metod som accepterar ctx som argument
    public void getReviewsByToiletIdFromContext(Context ctx) throws FileNotFoundException {
        getToiletsAnew();

        String tidParam = ctx.queryParam("toiletId");
        if (tidParam == null) {
            ctx.status(400).result("Missing query param: toiletId");
            return;
        }

        int toiletId = Integer.parseInt(tidParam);
        List<Review> reviews = getReviewsByToiletId(toiletId);
        ctx.json(reviews);
    }

    public String getAverageRatingByToiletId(int toiletId) throws FileNotFoundException {
        return String.valueOf(reviewsCollection.getAverageRating(toiletId));
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {//jordens diameter i meter
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Double.parseDouble(R) * c;
    }


}
