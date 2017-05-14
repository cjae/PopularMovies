package com.cjae.popularmovies.rest;

import com.cjae.popularmovies.model.MoviesWrapper;
import com.cjae.popularmovies.model.ReviewsWrapper;
import com.cjae.popularmovies.model.TrailerWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jedidiah on 18/04/2017.
 */

public interface APIClient {

    //--GET REQUEST---------------------------------------------------------------------------------
    @GET("movie/{sort_type}")
    Call<MoviesWrapper> getMovies(@Path("sort_type") String sort_type,
                                  @Query("api_key") String api_key,
                                  @Query("page") String page);

    @GET("movie/{id}/videos")
    Call<TrailerWrapper> getVideos(@Path("id") String id,
                                   @Query("api_key") String api_key);

    @GET("movie/{id}/reviews")
    Call<ReviewsWrapper> getReviews(@Path("id") String id,
                                    @Query("api_key") String api_key);
}
