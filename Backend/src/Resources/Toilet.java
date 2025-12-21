package Resources;

import java.util.List;

public class Toilet {
    private int id;
    private String name;
    private double lng;
    private double lat;
    private int change_table_child;
    private String fee;
    private String report_phoneNbr;
    private String report_url;
    private String openingHrs;
    private String openingSeason;
    private int nbrOfToilets;
    private String accessible;
    private List<Review> reviews;



    public Toilet(int id, String name, double lng, double lat, int change_table_child, String fee, String report_phoneNbr,
                  String report_url, String openingHrs, String openingSeason, int nbrOfToilets, String accessible, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.change_table_child = change_table_child;
        this.fee = fee;
        this.report_phoneNbr = report_phoneNbr;
        this.report_url = report_url;
        this.openingHrs = openingHrs;
        this.openingSeason = openingSeason;
        this.nbrOfToilets = nbrOfToilets;
        this.accessible = accessible;
        this.reviews = reviews;
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

    public String getReport_phoneNbr() {
        return report_phoneNbr;
    }

    public void setReport_phoneNbr(String report_phoneNbr) {
        this.report_phoneNbr = report_phoneNbr;
    }

    public String getReport_url() {
        return report_url;
    }

    public void setReport_url(String report_url) {
        this.report_url = report_url;
    }

    public String getOpeningHrs() {
        return openingHrs;
    }

    public void setOpeningHrs(String openingHrs) {
        this.openingHrs = openingHrs;
    }

    public String getOpeningSeason() {
        return openingSeason;
    }

    public void setOpeningSeason(String openingSeason) {
        this.openingSeason = openingSeason;
    }

    public int getNbrOfToilets() {
        return nbrOfToilets;
    }

    public void setNbrOfToilets(int nbrOfToilets) {
        this.nbrOfToilets = nbrOfToilets;
    }

    public String isAccessible() {
        return accessible;
    }

    public void setAccessible(String accessible) {
        this.accessible = accessible;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
