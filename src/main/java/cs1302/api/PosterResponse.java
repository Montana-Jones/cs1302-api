package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a response from the Movie Database Alternative API.
 */
public class PosterResponse {
    @SerializedName("Search") PosterResult[] search;
    int totalResults;
    @SerializedName("Response") boolean response;
} //PosterResponse
