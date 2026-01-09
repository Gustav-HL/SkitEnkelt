package Resources;

public class Review {
    private int id;
    private String author;
    private String date;
    private int toiletId;
    private String toiletName;
    private double rating;
    private String description;
    private String photo;

    public Review(int id, String author, String date, int toiletId, String toalettNamn, double rating, String description, String photo){
        this.id = id;
        this.author = author;
        this.date = date;
        this.toiletId = toiletId;
        this.toiletName = toalettNamn;
        this.rating = rating;
        this.description = description;
        this.photo = photo;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public int getToiletId(){
        return toiletId;
    }

    public void setToiletId(int toiletId){
        this.toiletId = toiletId;
    }

    public String getToiletName() {
        return toiletName;
    }

    public void setToiletName(String toaNamn) {
        this.toiletName = toaNamn;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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
