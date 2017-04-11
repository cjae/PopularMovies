package com.cjae.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String _adult;
    private String _backdrop_path;
    private String _id;
    private String _original_title;
    private String _release_date;
    private String _poster_path;
    private String _popularity;
    private String _title;
    private String _vote_average;
    private String _vote_count;
    private String _overview;
    private boolean _movieReady;

    public Movie(String _adult, String _backdrop_path, String _id, String _original_title,
                 String _release_date, String _poster_path, String _popularity, String _title,
                 String _vote_average, String _vote_count, String _overview, boolean _movieReady) {
        this._adult = _adult;
        this._backdrop_path = _backdrop_path;
        this._id = _id;
        this._original_title = _original_title;
        this._release_date = _release_date;
        this._poster_path = _poster_path;
        this._popularity = _popularity;
        this._title = _title;
        this._vote_average = _vote_average;
        this._vote_count = _vote_count;
        this._overview = _overview;
        this._movieReady = _movieReady;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._adult);
        dest.writeString(this._backdrop_path);
        dest.writeString(this._id);
        dest.writeString(this._original_title);
        dest.writeString(this._release_date);
        dest.writeString(this._poster_path);
        dest.writeString(this._popularity);
        dest.writeString(this._title);
        dest.writeString(this._vote_average);
        dest.writeString(this._vote_count);
        dest.writeString(this._overview);
        dest.writeByte(this._movieReady ? (byte) 1 : (byte) 0);
    }

    private Movie(Parcel in) {
        this._adult = in.readString();
        this._backdrop_path = in.readString();
        this._id = in.readString();
        this._original_title = in.readString();
        this._release_date = in.readString();
        this._poster_path = in.readString();
        this._popularity = in.readString();
        this._title = in.readString();
        this._vote_average = in.readString();
        this._vote_count = in.readString();
        this._overview = in.readString();
        this._movieReady = in.readByte() != 0;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "_adult='" + _adult + '\'' +
                ", _backdrop_path='" + _backdrop_path + '\'' +
                ", _id='" + _id + '\'' +
                ", _original_title='" + _original_title + '\'' +
                ", _release_date='" + _release_date + '\'' +
                ", _poster_path='" + _poster_path + '\'' +
                ", _popularity='" + _popularity + '\'' +
                ", _title='" + _title + '\'' +
                ", _vote_average='" + _vote_average + '\'' +
                ", _vote_count='" + _vote_count + '\'' +
                ", _overview='" + _overview + '\'' +
                ", _movieReady=" + _movieReady +
                '}';
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
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
