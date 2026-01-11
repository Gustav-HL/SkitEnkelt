package Resources;

import java.util.List;

public class Toilet {
    private int id;
    private String name;
    private double lng;
    private double lat;
    private int change_table_child;
    private String fee;
    private int nbrWcs;
    private List<Review> reviews;
    private double rating;
    private String openingHours;
    private int distance;

    public Toilet(int id, String name, double lng, double lat, int change_table_child, String fee, int nbrWcs, List<Review> reviews, String rating, String openingHours) {
        this.id = id;
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.change_table_child = change_table_child;
        this.fee = fee;
        this.nbrWcs = nbrWcs;
        this.reviews = reviews;
        this.openingHours = openingHours;
        this.rating = Double.parseDouble(rating);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getChange_table_child() {
        return change_table_child;
    }

    public void setChange_table_child(int change_table_child) {
        this.change_table_child = change_table_child;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public int getNbrWcs() {
        return nbrWcs;
    }

    public void setNbrWcs(int nbrWcs){
        this.nbrWcs = nbrWcs;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = (int) Math.round(distance);
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
