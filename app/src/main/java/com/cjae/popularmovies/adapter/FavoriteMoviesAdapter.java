package com.cjae.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjae.popularmovies.R;
import com.cjae.popularmovies.data.MoviesColumns;
import com.cjae.popularmovies.listener.FavoriteRecyclerClickListener;
import com.cjae.popularmovies.listener.RecyclerViewClickListener;
import com.cjae.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.cjae.popularmovies.utils.NetworkUtils.BASE_IMAGE_URL;

/**
 * Created by Jedidiah on 14/05/2017.
 */

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.MovieViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private FavoriteRecyclerClickListener mOnClickListener;

    public FavoriteMoviesAdapter(FavoriteRecyclerClickListener mOnClickListener) {
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
        // Indices for the title, poster
        int titleIndex = mCursor.getColumnIndex(MoviesColumns.COLUMN_MOVIE_TITLE);
        int posterIndex = mCursor.getColumnIndex(MoviesColumns.COLUMN_MOVIE_POSTER);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final String title = mCursor.getString(titleIndex);
        final String poster = mCursor.getString(posterIndex);

        //Set values
        viewHolder.movieTitle.setText(title);

        Picasso.with(mContext)
                .load(BASE_IMAGE_URL + poster)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(viewHolder.postalView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(BASE_IMAGE_URL + poster)
                                .into(viewHolder.postalView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
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
            mOnClickListener.onListItemClick(doGetMovieAtPosition(getAdapterPosition()));
        }

        private String doGetMovieAtPosition(int position) {
            // Indices for the _id
            int idIndex = mCursor.getColumnIndex(MoviesColumns.COLUMN_MOVIE_ID);

            mCursor.moveToPosition(position); // get to the right location in the cursor

            return mCursor.getString(idIndex);
        }
    }
}
