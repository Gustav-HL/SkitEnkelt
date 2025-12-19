package Resources;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
    public String type;
    public String name;
    public List<Feature> features;
    public List<Review> reviews;

    public FeatureCollection(){
        this.type = "";
        this.name = "";
        this.features = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
