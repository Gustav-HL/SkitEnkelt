package Resources;

public class Toilet {
    private int id;
    private String name;
    private double lng;
    private double lat;
    private int change_table_child;
    private String fee;

    public Toilet(int id, String name, double lng, double lat, int change_table_child, String fee) {
        this.id = id;
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.change_table_child = change_table_child;
        this.fee = fee;
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
}
