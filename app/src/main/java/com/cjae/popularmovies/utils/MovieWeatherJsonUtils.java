package com.cjae.popularmovies.utils;

import com.cjae.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jedidiah on 13/04/2017.
 */

public class MovieWeatherJsonUtils {

    public static ArrayList<Movie> getSimpleMoveStringsFromJson(String movieJsonStr)
            throws JSONException {

        final String OWM_PAGE = "page";
        final String OWM_RESULTS = "results";
        final String OWM_TOTAL_RESULTS = "total_results";
        final String OWM_TOTAL_PAGES = "total_pages";

        ArrayList<Movie> parsedMovieData = new ArrayList<>();

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieResultsArray = movieJson.getJSONArray(OWM_RESULTS);

        for (int i = 0; i < movieResultsArray.length(); i++) {
            /* These are the values that will be collected */
            int id;
            String originalTitle;
            String postalPath;
            String overview;
            double vote_average;
            String release_date;

            /* Get the JSON object representing the movie */
            JSONObject movieItem = movieResultsArray.getJSONObject(i);

            id = movieItem.getInt("id");
            originalTitle = movieItem.getString("original_title");
            postalPath = movieItem.getString("poster_path");
            overview = movieItem.getString("overview");
            vote_average = movieItem.getDouble("vote_average");
            release_date = movieItem.getString("release_date");

            parsedMovieData.add(new Movie(id, originalTitle, postalPath, overview, vote_average, release_date));
        }

        return parsedMovieData;
    }
}
