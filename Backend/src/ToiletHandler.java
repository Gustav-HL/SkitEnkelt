import Resources.Toilet;
import io.javalin.http.Context;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    public ToiletHandler() throws FileNotFoundException {
        getToiletsAnew();
    }

    public void getToiletsAnew() throws FileNotFoundException {
        JsonReader reader = new JsonReader(new StringReader(callToiletsAPI()));
        JsonReader reviewReader = new JsonReader(new FileReader("./reviews.json"));
        Gson gson = new Gson();
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
                .map(f ->

                        {
                            try {
                                return new Toilet(
                                        f.properties.id,
                                        f.properties.name,
                                        f.geometry.coordinates.get(0),
                                        f.geometry.coordinates.get(1),
                                        f.properties.change_table_child,
                                        f.properties.fee,
                                        f.properties.wc,
                                        getReviewsForSelectedToilet(f.properties.id)
                                );
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                //Filtering away toilets with no change tables if the query is passed
                .filter(t -> ctx.queryParam("change_table_child") == null || t.getChange_table_child() > 0)
                //Filtering away toilets with a fee if the query is passed
                .filter(t -> ctx.queryParam("fee") == null || t.getFee().isBlank())
                //Filtering away toilets without the given amount of wcs if the query is passed
                .filter(t -> ctx.queryParam("wc") == null || t.getNbrWcs() >= Integer.parseInt(Objects.requireNonNull(ctx.queryParam("wc"))))
                //Proximity filter
                .filter(t -> calculateDistance(
                        lat,
                        lon,
                        t.getLat(),
                        t.getLng()
                ) <= range)
                .sorted((a, b) -> {
                    double distA = calculateDistance(lat, lon, a.getLat(), a.getLng());
                    double distB = calculateDistance(lat, lon, b.getLat(), b.getLng());
                    return Double.compare(distA, distB);
                })
                .collect(Collectors.toList());



        int numberOfShittersCloseBy = 0;
        int numbersOfChangingTables = 0;
        for (Toilet t : toilets) {
            numberOfShittersCloseBy += t.getNbrWcs();
            numbersOfChangingTables += t.getChange_table_child();

            double distance = calculateDistance(lat, lon, t.getLat(), t.getLng());
            System.out.println(
                    t.getName() + " → " + Math.round(distance) + " meters"
            );
        }
        System.out.println("Number of toilets in range: " + numberOfShittersCloseBy);
        System.out.println("Number of changing tables in range: " + numbersOfChangingTables);



        ctx.json(toilets);
    }

    public void addReview(int id, String author,
                          String date, int toiletId, String toiletName, double rating, String description, String photo) {
        String reviewJson = """
    {
      "id": "%s",
      "author": "%s",
      "date": "%s",
      "toiletId": "%s",
      "toiletName": "%s",
      "rating": %s,
      "description": "%s",
      "photo": "%s"
    }
    """.formatted(
                id,
                escapeJson(author),
                escapeJson(date),
                toiletId,
                escapeJson(toiletName),
                rating,
                escapeJson(description),
                escapeJson(photo)

        );

        Path file = Path.of("reviews.json");

        try {
            Files.writeString(
                    file,
                    reviewJson + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Minimal JSON string escaping so quotes/newlines don't break JSON
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
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
