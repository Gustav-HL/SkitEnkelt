package Resources;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
    public String type;
    public String name;
    public List<Feature> features;

    public FeatureCollection(){
        this.type = "";
        this.name = "";
        this.features = new ArrayList<>();
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
