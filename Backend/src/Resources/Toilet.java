package Resources;

public class Toilet {
    int id;
    String name;
    double lng;
    double lat;
    int nbrOfToilets;
    String fee;

    public Toilet(int id, String name, double lng, double lat, int nbrOfToilets, String fee) {
        this.id = id;
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.nbrOfToilets = nbrOfToilets;
        this.fee = fee;
    }

    public String getName() {
        return name;
    }
}
