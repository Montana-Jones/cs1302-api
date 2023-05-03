package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a response from the Movie Database Alternative API.
 */
public class OMDBResponse {
    @SerializedName("Response") boolean response;
    @SerializedName("Error") String error;
} //OMDBResponse
