package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a result in a response from the Movie Database Alternative API.
 */
public class OMDBResult {
    @SerializedName("Title") String title;
    @SerializedName("Year") int year;
    @SerializedName("Rated") String rated;
    @SerializedName("Released") String released;
    @SerializedName("Runtime") String runtime;
    @SerializedName("Genre") String genre;
    @SerializedName("Director") String director;
    @SerializedName("Writer") String writer;
    @SerializedName("Actors") String actor;
    @SerializedName("Plot") String plot;
    @SerializedName("Language") String language;
    @SerializedName("Country") String country;
    @SerializedName("Awards") String awards;
    @SerializedName("Ratings") Object[] ratings;
    @SerializedName("Metascore") int metascore;
    @SerializedName("Poster") String poster;
    double imdbRating;
    int imdbVotes;
    String imdbID;
    @SerializedName("Type") String type;
    @SerializedName("DVD") String dvd;
    @SerializedName("BoxOffice") int boxOffice;
    @SerializedName("Production") String production;
    @SerializedName("Website") String website;
    @SerializedName("Response") boolean response;
} //OMDBResult
