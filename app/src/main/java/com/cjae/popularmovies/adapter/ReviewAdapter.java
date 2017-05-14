package com.cjae.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjae.popularmovies.R;
import com.cjae.popularmovies.listener.TrailerClickListener;
import com.cjae.popularmovies.model.Review;
import com.cjae.popularmovies.model.Trailer;

import java.util.ArrayList;

/**
 * Created by Jedidiah on 14/05/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> mReviews;

    public ReviewAdapter(ArrayList<Review> mReviews) {
        this.mReviews = mReviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review selectedReview = mReviews.get(position);

        holder.author.setText(selectedReview.getAuthor());
        holder.content.setText(selectedReview.getContent().trim());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void appendReviews(ArrayList<Review> reviews) {
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView content;

        ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.review_author);
            content = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}
