package cs1302.api;

/**
 * Represents a response from the IMDb Search API. This is used by Gson to
 * create an object from the JSON response body.
 */
public class IMDBResult {
    String id;
    String image;
    String title;
    String description;
    String runtimeStr;
    String genres;
    Object[] genreList;
    String contentRating;
    double imDbRating;
    int imDbRatingVotes;
    int metacriticRating;
    String plot;
    String stars;
    Object[] starList;
} //IMDBResult
