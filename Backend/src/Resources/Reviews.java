package Resources;

import io.javalin.http.util.JsonEscapeUtil;

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

    public double getAverageRating(int toiletID){
        double totalRating = 0;
        int nbrOfReviews = 0;
        for(Review r : reviews){
            if(r.getToiletId() == toiletID){
                nbrOfReviews ++;
                totalRating += r.getRating();
            };
        }
        if(nbrOfReviews == 0 ){
            return 0;
        } else {
            return totalRating/nbrOfReviews;
        }
    }

    public double getAverageShittyness(int toiletID){
        double shittyness = 0;
        int nbrOfReviews = 0;
        for(Review r : reviews){
            if(r.getToiletId() == toiletID){
                nbrOfReviews ++;
                shittyness += r.getShittyness();
            };
        }


        if(nbrOfReviews == 0 ){
            return 0;
        } else {
            return shittyness/nbrOfReviews;
        }

    }

    public void getAllShittyness(){
        for(Review r : reviews){
            System.out.println(r.getToiletName() + " rating: " +  r.getShittyness());
            System.out.println("average shittyness rating: " + getAverageRating(r.getToiletId()));
        }

    }

}