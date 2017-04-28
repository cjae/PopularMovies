package com.cjae.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjae.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.cjae.popularmovies.utils.NetworkUtils.BASE_COVER_URL;
import static com.cjae.popularmovies.utils.NetworkUtils.BASE_IMAGE_URL;

public class DetailsActivity extends AppCompatActivity implements
        Palette.PaletteAsyncListener {

    @Bind(R.id.activity_detail_title)
    TextView activity_detail_title;

    @Bind(R.id.synopsis_title)
    TextView synopsis_title;

    @Bind(R.id.synopsis_tv)
    TextView synopsis_tv;
//
//    @Bind(R.id.release_date)
//    TextView release_date;
//
//    @Bind(R.id.rating_tv)
//    TextView rating_tv;
//
//    @Bind(R.id.thumbnail)
//    ImageView thumbnail;

    @Bind(R.id.fab)
    FloatingActionButton mFabButton;

    @Bind(R.id.info_container)
    View mInformationContainer;

    @Bind(R.id.item_movie_cover)
    ImageView item_movie_cover;

    @Nullable
    @Bind(R.id.item_movie_postal)
    ImageView item_movie_postal;

    Context context;

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
            Movie movie = intent.getParcelableExtra("movieItem");

            activity_detail_title.setText(movie.getOriginal_title());
            synopsis_tv.setText(movie.getOverview());
//
//            String dateString = String.format(getString(R.string.date_format), movie.get_release_date());
//            release_date.setText(dateString);
//
//            String ratingString = String.format(getString(R.string.rating_format), movie.get_vote_average());
//            rating_tv.setText(ratingString);

            showCoverImage(movie);
            showPostalImage(movie);
        }
    }

    private void showCoverImage(Movie movie) {
        Picasso.with(context)
                .load(BASE_COVER_URL + movie.getBackdrop_path())
                .placeholder(R.drawable.image_placeholder)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        item_movie_cover.setImageBitmap(bitmap);
                        Palette.from(bitmap)
                                .generate(DetailsActivity.this);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
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

    @Override
    public void onGenerated(Palette palette) {
        if (palette != null) {
            final Palette.Swatch darkVibrantSwatch    = palette.getDarkVibrantSwatch();
            final Palette.Swatch darkMutedSwatch      = palette.getDarkMutedSwatch();
            final Palette.Swatch lightVibrantSwatch   = palette.getLightVibrantSwatch();
            final Palette.Swatch lightMutedSwatch     = palette.getLightMutedSwatch();
            final Palette.Swatch vibrantSwatch        = palette.getVibrantSwatch();

            final Palette.Swatch backgroundAndContentColors = (darkVibrantSwatch != null)
                    ? darkVibrantSwatch : darkMutedSwatch;

            final Palette.Swatch titleAndFabColors = (darkVibrantSwatch != null)
                    ? lightVibrantSwatch : lightMutedSwatch;

            setBackgroundAndContentColors(backgroundAndContentColors);
            setHeadersTitleColors(titleAndFabColors);
            setVibrantElements(vibrantSwatch);
        }
    }

    public void setBackgroundAndContentColors(Palette.Swatch swatch) {
        if (swatch != null) {
            mInformationContainer.setBackgroundColor(swatch.getRgb());

            synopsis_tv.setTextColor(swatch.getBodyTextColor());
        } // else use colors of the layout
    }

    public void setVibrantElements(Palette.Swatch vibrantSwatch) {
        if(vibrantSwatch != null) {
            mFabButton.getBackground().setColorFilter(vibrantSwatch.getRgb(),
                    PorterDuff.Mode.MULTIPLY);
        } // else use colors of the layout
    }

    public void setHeadersTitleColors(Palette.Swatch swatch) {
        if (swatch != null) {
            synopsis_title.setTextColor(swatch.getRgb());
        }  // else use colors of the layout
    }
}
