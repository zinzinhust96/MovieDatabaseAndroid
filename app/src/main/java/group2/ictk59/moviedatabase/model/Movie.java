package group2.ictk59.moviedatabase.model;

/**
 * Created by ZinZin on 3/24/2017.
 */

public class Movie {

    private Long id;
    private String title;
    private String year;
    private String released;
    private String runtime;
    private String country;
    private String genre;
    private String director;
    private String casts;
    private String plot;
    private String poster;
    private String rating;

    public Movie(Long id, String title, String year, String released, String runtime, String country, String genre, String director, String casts, String plot, String poster, String rating) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.released = released;
        this.runtime = runtime;
        this.country = country;
        this.genre = genre;
        this.director = director;
        this.casts = casts;
        this.plot = plot;
        this.poster = poster;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getCountry() {
        return country;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public String getCasts() {
        return casts;
    }

    public String getPlot() {
        return plot;
    }

    public String getPoster() {
        return poster;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", released='" + released + '\'' +
                ", runtime='" + runtime + '\'' +
                ", country='" + country + '\'' +
                ", genre='" + genre + '\'' +
                ", director='" + director + '\'' +
                ", casts='" + casts + '\'' +
                ", plot='" + plot + '\'' +
                ", poster='" + poster + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
