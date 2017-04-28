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
    public static final String BASE_COVER_URL = "http://image.tmdb.org/t/p/w342";

    public static final String SORT_POPULAR = "popular";
    public static final String SORT_TOP_RATED = "top_rated";

    public static final String API_KEY = "87a901020f496977f9d6d508c5d186ec";
}
