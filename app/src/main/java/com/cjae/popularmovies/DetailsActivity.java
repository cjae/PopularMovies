package com.cjae.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjae.popularmovies.adapter.ReviewAdapter;
import com.cjae.popularmovies.adapter.TrailerAdapter;
import com.cjae.popularmovies.data.MoviesColumns;
import com.cjae.popularmovies.data.MoviesContentProvider;
import com.cjae.popularmovies.listener.TrailerClickListener;
import com.cjae.popularmovies.model.Movie;
import com.cjae.popularmovies.model.MoviesWrapper;
import com.cjae.popularmovies.model.Review;
import com.cjae.popularmovies.model.ReviewsWrapper;
import com.cjae.popularmovies.model.Trailer;
import com.cjae.popularmovies.model.TrailerWrapper;
import com.cjae.popularmovies.rest.APIClient;
import com.cjae.popularmovies.rest.ServiceGenerator;
import com.cjae.popularmovies.utils.CommonUtils;
import com.cjae.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.cjae.popularmovies.utils.NetworkUtils.BASE_COVER_URL;
import static com.cjae.popularmovies.utils.NetworkUtils.BASE_IMAGE_URL;
import static java.security.AccessController.getContext;

public class DetailsActivity extends AppCompatActivity implements
        TrailerClickListener {

    @Bind(R.id.activity_detail_title)
    TextView activity_detail_title;

    @Bind(R.id.genre_vw)
    TextView genre_vw;

    @Bind(R.id.release_date)
    TextView date_title;

    @Bind(R.id.language_vw)
    TextView language_vw;

    @Bind(R.id.rating_vw)
    TextView rating_vw;

    @Bind(R.id.synopsis_title)
    TextView synopsis_title;

    @Bind(R.id.trailer_title)
    TextView trailer_title;

    @Bind(R.id.review_title)
    TextView review_title;

    @Bind(R.id.synopsis_tv)
    TextView synopsis_tv;

    @Bind(R.id.fav_fab)
    FloatingActionButton mFabButton;

    @Bind(R.id.info_container)
    View mInformationContainer;

    @Bind(R.id.item_movie_cover)
    ImageView item_movie_cover;

    @Nullable
    @Bind(R.id.item_movie_postal)
    ImageView item_movie_postal;

    @Bind(R.id.trailer_rv)
    RecyclerView trailer_rv;

    @Bind(R.id.reviews_rv)
    RecyclerView reviews_rv;

    @Bind(R.id.loading_reviews_vw)
    TextView loading_reviews_vw;

    @Bind(R.id.loading_trailer_vw)
    TextView loading_trailer_vw;

    Context context;
    Movie movie;

    TrailerAdapter mTrailerAdapter;
    private ArrayList<Trailer> mTrailerList;

    ReviewAdapter mReviewAdapter;
    private ArrayList<Review> mReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        context = this;
        ButterKnife.bind(this);
        setUpViews();
    }

    private void setUpViews() {
        Intent intent = getIntent();

        if(intent.hasExtra("movieItem")) {
            movie = intent.getParcelableExtra("movieItem");

            activity_detail_title.setText(movie.getOriginal_title());
            synopsis_tv.setText(movie.getOverview());

            String dateString = String.format(getString(R.string.release_date),
                    movie.getRelease_date());
            date_title.setText(dateString);

            String ratingString = String.format(getString(R.string.rating_text),
                    String.valueOf(movie.getVote_average()));
            rating_vw.setText(ratingString);

            String languageString = String.format(getString(R.string.language),
                    movie.getOriginal_language().toUpperCase());
            language_vw.setText(languageString);

            String genreString = CommonUtils.changeGenreToString(movie.getGenre_ids());
            genre_vw.setText(genreString);

            showCoverImage(movie);
            showPostalImage(movie);

            doSetUpTrailerView(movie);
            doSetUpReviewView(movie);
        }
    }

    private void doSetUpReviewView(Movie movie) {
        mReviewsList = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(mReviewsList);

        reviews_rv.setHasFixedSize(true);
        LinearLayoutManager mlayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        reviews_rv.setLayoutManager(mlayoutManager);
        reviews_rv.setNestedScrollingEnabled(false);
        reviews_rv.setAdapter(mReviewAdapter);

        if(CommonUtils.isNetworkAvailable(this))
            doGetMovieReviews(movie.getId());
    }

    private void doSetUpTrailerView(Movie movie) {
        mTrailerList = new ArrayList<>();
        mTrailerAdapter = new TrailerAdapter(mTrailerList, this);

        trailer_rv.setHasFixedSize(true);
        LinearLayoutManager mlayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        trailer_rv.setLayoutManager(mlayoutManager);
        trailer_rv.setAdapter(mTrailerAdapter);

        if(CommonUtils.isNetworkAvailable(this))
            doGetMovieTrailers(movie.getId());
    }

    private void showCoverImage(Movie movie) {
        Picasso.with(context)
                .load(BASE_COVER_URL + movie.getBackdrop_path())
                .placeholder(R.drawable.image_placeholder)
                .into(item_movie_cover);
    }

    private void showPostalImage(final Movie movie) {
        if (item_movie_postal != null) {
            Picasso.with(context)
                    .load(BASE_IMAGE_URL + movie.getPoster_path())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(item_movie_postal, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Picasso.with(context)
                                    .load(BASE_IMAGE_URL + movie.getPoster_path())
                                    .into(item_movie_postal);
                        }
                    });
        }
    }

    private void doGetMovieTrailers(Integer id) {
        APIClient apiService = ServiceGenerator.getClient().create(APIClient.class);
        Call<TrailerWrapper> call = apiService.getVideos(String.valueOf(id), NetworkUtils.API_KEY);
        call.enqueue(new retrofit2.Callback<TrailerWrapper>() {
            @Override
            public void onResponse(Call<TrailerWrapper> call, Response<TrailerWrapper> response) {
                doInitiateTrailerList(response.body().getResults());
            }

            @Override
            public void onFailure(Call<TrailerWrapper> call, Throwable t) {

            }
        });
    }

    private void doInitiateTrailerList(ArrayList<Trailer> results) {
        if (results != null) {
            showTrailerData();
            mTrailerList = results;
            mTrailerAdapter.appendTrailers(mTrailerList);
        }
    }

    private void doGetMovieReviews(Integer id) {
        APIClient apiService = ServiceGenerator.getClient().create(APIClient.class);
        Call<ReviewsWrapper> call = apiService.getReviews(String.valueOf(id), NetworkUtils.API_KEY);
        call.enqueue(new retrofit2.Callback<ReviewsWrapper>() {
            @Override
            public void onResponse(Call<ReviewsWrapper> call, Response<ReviewsWrapper> response) {
                doInitiateReviewList(response.body().getResults());
            }

            @Override
            public void onFailure(Call<ReviewsWrapper> call, Throwable t) {

            }
        });
    }

    private void doInitiateReviewList(ArrayList<Review> results) {
        if (results != null) {
            showReviewData();
            mReviewsList = results;
            mReviewAdapter.appendReviews(mReviewsList);
        }
    }

    private void showReviewData() {
        loading_reviews_vw.setVisibility(View.INVISIBLE);
        reviews_rv.setVisibility(View.VISIBLE);
    }

    private void showTrailerData() {
        loading_trailer_vw.setVisibility(View.INVISIBLE);
        trailer_rv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fav_fab)
    void onClickFavoriteMovie() {
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(MoviesColumns.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(MoviesColumns.COLUMN_MOVIE_TITLE, movie.getOriginal_title());
        contentValues.put(MoviesColumns.COLUMN_MOVIE_COVER, movie.getBackdrop_path());
        contentValues.put(MoviesColumns.COLUMN_MOVIE_POSTER, movie.getPoster_path());
        contentValues.put(MoviesColumns.COLUMN_SYNOPSIS, movie.getOverview());
        contentValues.put(MoviesColumns.COLUMN_USER_RATING, String.valueOf(movie.getVote_average()));
        contentValues.put(MoviesColumns.COLUMN_RELEASE_DATE, movie.getRelease_date());
        contentValues.put(MoviesColumns.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginal_language());

        String genreString = CommonUtils.changeGenreToString(movie.getGenre_ids());
        contentValues.put(MoviesColumns.COLUMN_GENRE, genreString);

        try {
            // Insert the content values via a ContentResolver
            Uri uri = getContentResolver().insert(MoviesContentProvider.Movies.CONTENT_URI, contentValues);
            if(uri != null)
                Toast.makeText(this, getString(R.string.action_fav_text), Toast.LENGTH_SHORT).show();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, getString(R.string.action_added_fav_text), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(String key) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        intentBuilder.setExitAnimations(context, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(this, NetworkUtils.buildURI(key));
    }
}
