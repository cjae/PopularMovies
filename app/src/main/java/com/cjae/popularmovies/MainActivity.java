package com.cjae.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cjae.popularmovies.adapter.MovieRecyclerAdapter;
import com.cjae.popularmovies.listener.RecyclerViewClickListener;
import com.cjae.popularmovies.model.Movie;
import com.cjae.popularmovies.model.MoviesWrapper;
import com.cjae.popularmovies.rest.APIClient;
import com.cjae.popularmovies.rest.ServiceGenerator;
import com.cjae.popularmovies.utils.CommonUtils;
import com.cjae.popularmovies.utils.NetworkUtils;
import com.cjae.popularmovies.views.RecyclerInsetsDecoration;
import com.cjae.popularmovies.utils.SessionManager;
import com.cjae.popularmovies.views.AutofitRecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        RecyclerViewClickListener {

    private static final String LIFECYCLE_MOVIE_CALLBACKS_KEY = "movieList";
    private static final String LIFECYCLE_PAGE_NO_KEY = "page_no";

    @Bind(R.id.parent_coordinator)
    CoordinatorLayout parent_coordinator;

    @Bind(R.id.movies_recycler)
    AutofitRecyclerView mMovieRecyclerView;

    @Bind(R.id.pb_loading_indicator)
    ProgressBar mProgressBar;

    @Bind(R.id.no_network_error)
    View mNoNetworkView;

    private MovieRecyclerAdapter movieRecyclerAdapter;

    private Context mContext;

    Snackbar snackbar;

    private ArrayList<Movie> mMovieList;

    private boolean refreshLoading;

    private boolean loadingMore;

    int page_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        refreshLoading = false;
        loadingMore = false;
        mMovieList = new ArrayList<>();

        ButterKnife.bind(this);

        setUpViews();

        if(savedInstanceState == null || !savedInstanceState.containsKey(LIFECYCLE_MOVIE_CALLBACKS_KEY)
        || !savedInstanceState.containsKey(LIFECYCLE_PAGE_NO_KEY)) {
            loadMoviesData();
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(LIFECYCLE_MOVIE_CALLBACKS_KEY);
            page_no = savedInstanceState.getInt(LIFECYCLE_PAGE_NO_KEY);

            showMovieDataView();
            movieRecyclerAdapter.appendMovies(mMovieList);
        }
    }

    //Initiating required views
    private void setUpViews() {
        movieRecyclerAdapter = new MovieRecyclerAdapter(mMovieList, this);

        mMovieRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(this));
        addScrollListener();
        mMovieRecyclerView.setAdapter(movieRecyclerAdapter);
    }

    private void showMovieDataView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mNoNetworkView.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        mNoNetworkView.setVisibility(View.INVISIBLE);
    }

    private void showNetworkErrorView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        mNoNetworkView.setVisibility(View.VISIBLE);
    }

    private void addScrollListener() {
        mMovieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount    = mMovieRecyclerView.getLayoutManager().getChildCount();
                int totalItemCount      = mMovieRecyclerView.getLayoutManager().getItemCount();
                int pastVisibleItems    = ((GridLayoutManager) mMovieRecyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();

                if((visibleItemCount + pastVisibleItems) >= totalItemCount && !loadingMore) {
                    onLoadMoreData();
                }
            }
        });
    }

    // Refresh Data
    private void doRefreshData() {
        if(CommonUtils.isNetworkAvailable(this))
            if(!refreshLoading)
                doGetMoviesFromServer();
            else
                Toast.makeText(this, getString(R.string.no_network_text), Toast.LENGTH_SHORT).show();
    }

    // Initial Load Data from server
    private void loadMoviesData() {
        if(CommonUtils.isNetworkAvailable(this))
            doGetMoviesFromServer();
        else
            showNetworkErrorView();
    }

    private void doGetMoviesFromServer() {
        showProgressView();
        setRefreshLoading(true);
        page_no = 1;

        int sortType = SessionManager.getSortType(mContext);
        String mSortString;

        if(sortType == 0) {
            mSortString = NetworkUtils.SORT_POPULAR;
        } else {
            mSortString = NetworkUtils.SORT_TOP_RATED;
        }

        APIClient apiService = ServiceGenerator.getClient().create(APIClient.class);
        Call<MoviesWrapper> call = apiService.getMovies(mSortString, NetworkUtils.API_KEY, String.valueOf(page_no));
        call.enqueue(new Callback<MoviesWrapper>() {
            @Override
            public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                setRefreshLoading(false);
                doInitiateMovieList(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MoviesWrapper> call, Throwable t) {
                setRefreshLoading(false);
                showNetworkErrorView();
            }
        });
    }

    private void doInitiateMovieList(ArrayList<Movie> results) {
        if (results != null) {
            page_no++;
            showMovieDataView();
            mMovieList = results;
            movieRecyclerAdapter.resetMovies(mMovieList);
        }
    }

    // Load more data from server
    private void doLoadMoreMoviesFromServer(int page_no) {
        setLoadingMore(true);
        showLoadingSnackbar();

        int sortType = SessionManager.getSortType(mContext);
        String mSortString;

        if(sortType == 0) {
            mSortString = NetworkUtils.SORT_POPULAR;
        } else {
            mSortString = NetworkUtils.SORT_TOP_RATED;
        }

        APIClient apiService = ServiceGenerator.getClient().create(APIClient.class);
        Call<MoviesWrapper> call = apiService.getMovies(mSortString, NetworkUtils.API_KEY, String.valueOf(page_no));
        call.enqueue(new Callback<MoviesWrapper>() {
            @Override
            public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                setLoadingMore(false);
                dismissSnackbar();
                doUpdateMovieList(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MoviesWrapper> call, Throwable t) {
                setLoadingMore(false);
                dismissSnackbar();
                Toast.makeText(mContext, getString(R.string.error_text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoadingSnackbar() {
        snackbar = Snackbar
                .make(parent_coordinator, getString(R.string.action_loading), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void dismissSnackbar() {
        snackbar.dismiss();
    }

    private void doUpdateMovieList(ArrayList<Movie> results) {
        if (results != null) {
            page_no++;
            mMovieList.addAll(results);
            movieRecyclerAdapter.appendMovies(results);
        }
    }

    private void onLoadMoreData() {
        if(CommonUtils.isNetworkAvailable(this))
            doLoadMoreMoviesFromServer(page_no);
        else
            Toast.makeText(this, getString(R.string.no_network_text), Toast.LENGTH_SHORT).show();
    }

    private void setRefreshLoading(boolean value) {
        refreshLoading = value;
    }

    private void setLoadingMore(boolean value) {
        loadingMore = value;
    }

    @OnClick(R.id.retry_btn) void onClickRetryButton(){
        loadMoviesData();
    }

    @OnClick(R.id.fab) void onClickFavouriteButton() {
        startActivity(new Intent(this, FavoriteActivity.class));
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
            doRefreshData();
            return true;
        }

        if (id == R.id.action_sort) {
            doSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doSortDialog() {
        //Create sequence of items
        final String[] sortSequence = new String[]{"Popular", "Top rated"};
        final int sortId = SessionManager.getSortType(mContext);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Sort by");
        dialogBuilder.setSingleChoiceItems(sortSequence, sortId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(sortId == i) {
                            dialogInterface.dismiss();
                        } else {
                            SessionManager.setSortType(mContext, i);
                            loadMoviesData();
                            dialogInterface.dismiss();
                        }
                    }
                });

        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show(); //Show the dialog
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIFECYCLE_MOVIE_CALLBACKS_KEY, mMovieList);
        outState.putInt(LIFECYCLE_PAGE_NO_KEY, page_no);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie movie = mMovieList.get(clickedItemIndex);
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("movieItem", movie);
        startActivity(intent);

    }
}
