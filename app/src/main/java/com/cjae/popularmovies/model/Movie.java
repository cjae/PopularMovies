package com.cjae.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private int _id;
    private String _original_title;
    private String _poster_path;
    private String _overview;
    private double _vote_average;
    private String _release_date;

    public Movie(int _id, String _original_title, String _poster_path, String _overview,
                 double _vote_average, String _release_date) {
        this._id = _id;
        this._original_title = _original_title;
        this._poster_path = _poster_path;
        this._overview = _overview;
        this._vote_average = _vote_average;
        this._release_date = _release_date;
    }

    public int get_id() {
        return _id;
    }

    public String get_original_title() {
        return _original_title;
    }

    public String get_poster_path() {
        return _poster_path;
    }

    public String get_overview() {
        return _overview;
    }

    public double get_vote_average() {
        return _vote_average;
    }

    public String get_release_date() {
        return _release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this._original_title);
        dest.writeString(this._poster_path);
        dest.writeString(this._overview);
        dest.writeDouble(this._vote_average);
        dest.writeString(this._release_date);
    }

    private Movie(Parcel in) {
        this._id = in.readInt();
        this._original_title = in.readString();
        this._poster_path = in.readString();
        this._overview = in.readString();
        this._vote_average = in.readDouble();
        this._release_date = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
