package com.cjae.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.cjae.popularmovies.utils.SessionManager;
import com.cjae.popularmovies.views.AutofitRecyclerView;
import com.cjae.popularmovies.views.RecyclerInsetsDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jedidiah on 14/05/2017.
 */

public class MainFragment extends Fragment implements RecyclerViewClickListener {

    private static final String LIFECYCLE_MOVIE_CALLBACKS_KEY = "movieList";
    private static final String LIFECYCLE_PAGE_NO_KEY = "page_no";
    private static final String SORT_KEY = "sort_key";

    private MovieRecyclerAdapter movieRecyclerAdapter;

    private Context mContext;

    Snackbar mSnackbar;

    private ArrayList<Movie> mMovieList;

    private boolean loadingMore;

    int page_no;

    private String mSortKey;

    @Bind(R.id.content_main)
    FrameLayout parentContainer;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.movies_recycler)
    AutofitRecyclerView mMovieRecyclerView;

    @Bind(R.id.pb_loading_indicator)
    ProgressBar mProgressBar;

    @Bind(R.id.no_network_error)
    View mNoNetworkView;

    public static MainFragment getInstance(String sortKey) {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SORT_KEY, sortKey);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,v);
        setUpViews();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState == null) {
            loadMoviesData();
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(LIFECYCLE_MOVIE_CALLBACKS_KEY);
            page_no = savedInstanceState.getInt(LIFECYCLE_PAGE_NO_KEY);

            showMovieDataView();
            movieRecyclerAdapter.resetMovies(mMovieList);
        }
    }

    //Initiating required views
    private void setUpViews() {
        mContext = getActivity();
        loadingMore = false;
        mMovieList = new ArrayList<>();
        mSortKey = getArguments().getString(SORT_KEY);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark),
                ContextCompat.getColor(getActivity(),R.color.colorPrimary),
                ContextCompat.getColor(getActivity(),R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doGetMoviesFromServer();
            }
        });

        movieRecyclerAdapter = new MovieRecyclerAdapter(mMovieList, this);
        mMovieRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(mContext));
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

    private void onLoadMoreData() {
        if(CommonUtils.isNetworkAvailable(mContext))
            doLoadMoreMoviesFromServer(page_no);
        else
            Toast.makeText(mContext, getString(R.string.no_network_text), Toast.LENGTH_SHORT).show();
    }

    private void doGetMoviesFromServer() {
        page_no = 1;

        APIClient apiService = ServiceGenerator.getClient().create(APIClient.class);
        Call<MoviesWrapper> call = apiService.getMovies(mSortKey, NetworkUtils.API_KEY, String.valueOf(page_no));
        call.enqueue(new Callback<MoviesWrapper>() {
            @Override
            public void onResponse(Call<MoviesWrapper> call, Response<MoviesWrapper> response) {
                doInitiateMovieList(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MoviesWrapper> call, Throwable t) {
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

            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }

    // Load more data from server
    private void doLoadMoreMoviesFromServer(int page_no) {
        setLoadingMore(true);
        showLoadingSnackbar();

        APIClient apiService = ServiceGenerator.getClient().create(APIClient.class);
        Call<MoviesWrapper> call = apiService.getMovies(mSortKey, NetworkUtils.API_KEY, String.valueOf(page_no));
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
        mSnackbar = Snackbar
                .make(parentContainer, getString(R.string.action_loading), Snackbar.LENGTH_INDEFINITE);
        mSnackbar.show();
    }

    private void dismissSnackbar() {
        mSnackbar.dismiss();
    }

    private void doUpdateMovieList(ArrayList<Movie> results) {
        if (results != null) {
            page_no++;
            mMovieList.addAll(results);
            movieRecyclerAdapter.appendMovies(results);
        }
    }

    private void setLoadingMore(boolean value) {
        loadingMore = value;
    }

    // Initial Load Data from server
    private void loadMoviesData() {
        if(CommonUtils.isNetworkAvailable(mContext)) {
            showProgressView();
            doGetMoviesFromServer();
        } else
            showNetworkErrorView();
    }

    @OnClick(R.id.retry_btn)
    void onClickRetryButton(){
        loadMoviesData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LIFECYCLE_MOVIE_CALLBACKS_KEY, mMovieList);
        outState.putInt(LIFECYCLE_PAGE_NO_KEY, page_no);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie movie = mMovieList.get(clickedItemIndex);
        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra("movieItem", movie);
        startActivity(intent);
    }
}
