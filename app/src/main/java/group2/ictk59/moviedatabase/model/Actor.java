package group2.ictk59.moviedatabase.model;

import java.util.List;

/**
 * Created by ZinZin on 3/29/2017.
 */

public class Actor {

    private Long id;
    private String biography;
    private String birthday;
    private String deathday;
    private String name;
    private String placeOfBirth;
    private Double popularity;
    private String profilePic;
    private List<Movie> knownFor;

    public Actor(Long id, String biography, String birthday, String deathday, String name, String placeOfBirth, Double popularity, String profilePic, List<Movie> knownFor) {
        this.id = id;
        this.biography = biography;
        this.birthday = birthday;
        this.deathday = deathday;
        this.name = name;
        this.placeOfBirth = placeOfBirth;
        this.popularity = popularity;
        this.profilePic = profilePic;
        this.knownFor = knownFor;
    }

    public Long getId() {
        return id;
    }

    public String getBiography() {
        return biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public String getName() {
        return name;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public List<Movie> getKnownFor() {
        return knownFor;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", biography='" + biography + '\'' +
                ", birthday='" + birthday + '\'' +
                ", deathday='" + deathday + '\'' +
                ", name='" + name + '\'' +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", popularity=" + popularity +
                ", profilePic='" + profilePic + '\'' +
                ", knownFor=" + knownFor +
                '}';
    }
}
