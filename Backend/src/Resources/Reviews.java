package Resources;

import java.util.ArrayList;
import java.util.List;

public class Reviews {
    private List<Review> reviews;

    public Reviews(){
        this.reviews = new ArrayList<>();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}