package com.cjae.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cjae.popularmovies.adapter.FavoriteMoviesAdapter;
import com.cjae.popularmovies.data.MoviesContentProvider;
import com.cjae.popularmovies.listener.FavoriteRecyclerClickListener;
import com.cjae.popularmovies.model.Movie;
import com.cjae.popularmovies.views.AutofitRecyclerView;
import com.cjae.popularmovies.views.RecyclerInsetsDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jedidiah on 14/05/2017.
 */

public class FavoriteFragment extends Fragment implements
        FavoriteRecyclerClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MOVIES_LOADER_ID = 555;

    private FavoriteMoviesAdapter favoriteMoviesAdapter;

    private Context mContext;

    @Bind(R.id.fav_movies_recycler)
    AutofitRecyclerView mMovieRecyclerView;

    @Bind(R.id.fav_loading_indicator)
    ProgressBar mProgressBar;

    @Bind(R.id.no_content_vw)
    TextView no_content_vw;

    public static FavoriteFragment getInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this,v);
        setUpViews();
        return v;
    }

    //Initiating required views
    private void setUpViews() {
        favoriteMoviesAdapter = new FavoriteMoviesAdapter(this);
        mMovieRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(mContext));
        mMovieRecyclerView.setAdapter(favoriteMoviesAdapter);
    }

    private void showMovieDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        no_content_vw.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        no_content_vw.setVisibility(View.INVISIBLE);
    }

    private void showNoContentErrorView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        no_content_vw.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(mContext) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                showProgressView();
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {

                try {
                    return mContext.getContentResolver().query(MoviesContentProvider.Movies.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            showNoContentErrorView();
            return;
        }

        showMovieDataView();
        favoriteMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteMoviesAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(String movieId) {

    }
}
