package com.cjae.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cjae.popularmovies.R;
import com.cjae.popularmovies.listener.TrailerClickListener;
import com.cjae.popularmovies.model.Trailer;
import com.cjae.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jedidiah on 14/05/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Context mContext;
    private ArrayList<Trailer> mTrailerList;
    private TrailerClickListener trailerClickListener;

    public TrailerAdapter(ArrayList<Trailer> mTrailerList, TrailerClickListener trailerClickListener) {
        this.mTrailerList = mTrailerList;
        this.trailerClickListener = trailerClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder viewHolder, final int position) {
        final Trailer selectedTrailer = mTrailerList.get(position);

        final String thumbnail_url = NetworkUtils.BASE_YOUTUBE_THUMBNAIL +
                (selectedTrailer.getKey())+ "/mqdefault.jpg";

        Picasso.with(mContext)
                .load(thumbnail_url)
                .placeholder(R.drawable.image_placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(viewHolder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(thumbnail_url)
                                .into(viewHolder.thumbnail);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public void appendTrailers(ArrayList<Trailer> trailerList) {
        mTrailerList.addAll(trailerList);
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView thumbnail;

        TrailerViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Trailer trailer = mTrailerList.get(clickedPosition);
            trailerClickListener.onListItemClick(trailer.getKey());
        }
    }
}
