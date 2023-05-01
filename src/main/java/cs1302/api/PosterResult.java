package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a result in a response from the Movie Database Alternative API.
 */
public class PosterResult {
    @SerializedName("Title") String title;
    @SerializedName("Year") int year;
    String imdbID;
    @SerializedName("Poster") String poster;
} //PosterResult
