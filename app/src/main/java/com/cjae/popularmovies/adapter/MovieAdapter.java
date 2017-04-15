package com.cjae.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cjae.popularmovies.R;
import com.cjae.popularmovies.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.cjae.popularmovies.utils.NetworkUtils.BASE_IMAGE_URL;

/**
 * Created by Jedidiah on 06/04/2017.
 */

public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Movie> movieArrayList;

    public MovieAdapter(Context mContext, ArrayList<Movie> movieArrayList) {
        this.mContext = mContext;
        this.movieArrayList = movieArrayList;
    }

    @Override
    public int getCount() {
        return movieArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final Movie movie = movieArrayList.get(i);

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_item, viewGroup, false);
        }
        final ImageView postalView = (ImageView) convertView.findViewById(R.id.movie_postal);

        assert movie != null;
        Picasso.with(mContext)
                .load(BASE_IMAGE_URL + movie.get_poster_path())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(postalView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(BASE_IMAGE_URL + movie.get_poster_path())
                                .into(postalView);
                    }
                });


        return convertView;
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        movieArrayList = movieData;
        notifyDataSetChanged();
    }

//    public Movie getMovieData(int position) {
//        return movieArrayList.get(position);
//    }
}
