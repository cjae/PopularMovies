package com.cjae.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjae.popularmovies.R;
import com.cjae.popularmovies.listener.RecyclerViewClickListener;
import com.cjae.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.cjae.popularmovies.utils.NetworkUtils.BASE_IMAGE_URL;

/**
 * Created by Jedidiah on 27/04/2017.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mMovieList;
    private RecyclerViewClickListener mOnClickListener;

    public MovieRecyclerAdapter(ArrayList<Movie> mMovieList, RecyclerViewClickListener mOnClickListener) {
        this.mMovieList = mMovieList;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder viewHolder, final int position) {
        final Movie selectedMovie = mMovieList.get(position);

        viewHolder.movieTitle.setText(selectedMovie.getOriginal_title());

        Picasso.with(mContext)
                .load(BASE_IMAGE_URL + selectedMovie.getPoster_path())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(viewHolder.postalView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(BASE_IMAGE_URL + selectedMovie.getPoster_path())
                                .into(viewHolder.postalView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void appendMovies(ArrayList<Movie> movieList) {
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }

    public void resetMovies(ArrayList<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView postalView;
        TextView movieTitle;

        MovieViewHolder(View itemView) {
            super(itemView);
            postalView = (ImageView) itemView.findViewById(R.id.movie_postal);
            movieTitle = (TextView) itemView.findViewById(R.id.item_movie_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
