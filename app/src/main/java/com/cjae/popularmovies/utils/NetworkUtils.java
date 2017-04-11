package com.cjae.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jedidiah on 06/04/2017.
 */

public class NetworkUtils {
    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    private static final String PARAM_API_KEY_QUERY = "api_key";
    private static final String API_KEY = "87a901020f496977f9d6d508c5d186ec";

    private final static String PARAM_SORT = "sort_by";

    /**
     * Builds the URL used to query GitHub.
     *
     * @param sortQuery The keyword that will be queried for.
     * @return The URL to use to query the themoviedb.
     */
    public static URL buildUrl(String sortQuery) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY_QUERY, API_KEY)
                .appendQueryParameter(PARAM_SORT, sortQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
