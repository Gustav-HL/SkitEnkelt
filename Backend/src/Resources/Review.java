package Resources;

import java.util.Date;
import java.util.List;

public class Review {
    private String author;
    private Date date;
    private String toiletName;
    private List<Double> coordinates;
    private int rating;
    private String description;
    private String photo;

    public Review(String author, Date date, String toalettNamn, List<Double> coordinates, int rating, String description, String photo){
        this.author = author;
        this.date = date;
        this.toiletName = toalettNamn;
        this.coordinates = coordinates;
        this.rating = rating;
        this.description = description;
        this.photo = photo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getToiletName() {
        return toiletName;
    }

    public void setToiletName(String toaNamn) {
        this.toiletName = toaNamn;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
