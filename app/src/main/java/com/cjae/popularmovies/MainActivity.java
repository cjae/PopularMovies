package com.cjae.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.cjae.popularmovies.adapter.MovieAdapter;
import com.cjae.popularmovies.model.Movie;
import com.cjae.popularmovies.utils.MovieWeatherJsonUtils;
import com.cjae.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LIFECYCLE_MOVIE_CALLBACKS_KEY = "movieList";

    private GridView mGridView;

    private ProgressBar mProgressBar;

    private MovieAdapter mMovieAdapter;

    private ArrayList<Movie> mMovieList = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.movie_grid);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMovieAdapter = new MovieAdapter(this, mMovieList);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMovieList.get(i);

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("movieItem", movie);
                startActivity(intent);
            }
        });

        if(savedInstanceState == null || !savedInstanceState.containsKey(LIFECYCLE_MOVIE_CALLBACKS_KEY)) {
            loadMoviesData();
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(LIFECYCLE_MOVIE_CALLBACKS_KEY);
            mMovieAdapter.setMovieData(mMovieList);
        }
    }

    private void loadMoviesData() {
        new FetchMoviesTask().execute();
    }

    private void showMovieDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showProgressView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadMoviesData();
            return true;
        }

        if (id == R.id.action_sort) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIFECYCLE_MOVIE_CALLBACKS_KEY, mMovieList);
        super.onSaveInstanceState(outState);
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressView();
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            URL movieRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonWeatherResponse =NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                return MovieWeatherJsonUtils
                        .getSimpleMoveStringsFromJson(jsonWeatherResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayList) {
            if (movieArrayList != null) {
                showMovieDataView();
                mMovieList = movieArrayList;
                mMovieAdapter.setMovieData(mMovieList);
            }
        }
    }
}
