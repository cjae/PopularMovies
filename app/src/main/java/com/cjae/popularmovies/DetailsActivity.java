package com.cjae.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjae.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.cjae.popularmovies.utils.NetworkUtils.BASE_IMAGE_URL;

public class DetailsActivity extends AppCompatActivity {

    private TextView title_tv;
    private TextView synopsis_tv;
    private TextView release_date;
    private TextView rating_tv;
    private ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        title_tv = (TextView) findViewById(R.id.title_tv);
        synopsis_tv = (TextView) findViewById(R.id.synopsis_tv);
        release_date = (TextView) findViewById(R.id.release_date);
        rating_tv = (TextView) findViewById(R.id.rating_tv);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);

        setUpViews();
    }

    private void setUpViews() {
        Intent intent = getIntent();

        if(intent.hasExtra("movieItem")) {
            final Movie movie = intent.getParcelableExtra("movieItem");

            title_tv.setText(movie.get_original_title());
            synopsis_tv.setText(movie.get_overview());

            String dateString = String.format(getString(R.string.date_format), movie.get_release_date());
            release_date.setText(dateString);

            String ratingString = String.format(getString(R.string.rating_format), movie.get_vote_average());
            rating_tv.setText(ratingString);

            final Context context = getApplicationContext();

            Picasso.with(context)
                    .load(BASE_IMAGE_URL + movie.get_poster_path())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Picasso.with(context)
                                    .load(BASE_IMAGE_URL + movie.get_poster_path())
                                    .into(thumbnail);
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
}
