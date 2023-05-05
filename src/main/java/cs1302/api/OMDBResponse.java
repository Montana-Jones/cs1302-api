package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a response from the Movie Database Alternative API.
 */
public class OMDBResponse {
    @SerializedName("Title") String title;
    @SerializedName("Year") int year;
    @SerializedName("Rated") String rated;
    @SerializedName("Released") String released;
    @SerializedName("Runtime") String runtime;
    @SerializedName("Genre") String genre;
    @SerializedName("Director") String director;
    @SerializedName("Writer") String writer;
    @SerializedName("Actors") String actors;
    @SerializedName("Plot") String plot;
    @SerializedName("Language") String language;
    @SerializedName("Country") String country;
    @SerializedName("Awards") String awards;
    @SerializedName("Poster") String poster;
    @SerializedName("Ratings") Object[] ratings;
    @SerializedName("Metascore") int metascore;
    double imdbRating;
    String imdbVotes;
    String imdbID;
    @SerializedName("Type") String type;
    @SerializedName("DVD") String dvd;
    @SerializedName("BoxOffice") String boxOffice;
    @SerializedName("Production") String production;
    @SerializedName("Website") String website;
    @SerializedName("Response") boolean response;
    @SerializedName("Error") String error;
} //OMDBResponse
