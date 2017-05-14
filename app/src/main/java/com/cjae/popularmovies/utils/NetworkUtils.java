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
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_COVER_URL = "http://image.tmdb.org/t/p/w500";
    private static final String BASE_YOUTUBE_URI = "https://www.youtube.com";
    public static final String BASE_YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/";

    public static final String SORT_POPULAR = "popular";
    public static final String SORT_TOP_RATED = "top_rated";
    public static final String SORT_FAVORITE = "favorite";

    // Put your API KEY
    public static final String API_KEY = "";

    public static Uri buildURI(String key) {
        return Uri.parse(BASE_YOUTUBE_URI).buildUpon()
                .appendPath("watch")
                .appendQueryParameter("v", key)
                .build();
    }
}
