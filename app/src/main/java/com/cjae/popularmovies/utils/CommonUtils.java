package com.cjae.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by Jedidiah on 15/04/2017.
 */

public class CommonUtils {

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String changeGenreToString(List<Integer> genreIds) {
        String text = "";
        for(int  i = 0 ; i < genreIds.size() ; i++){
            int genreId = genreIds.get(i);
            if (i != genreIds.size() - 1)
                text += getGenreTitle(genreId) + " | ";
            else
                text += getGenreTitle(genreId);
        }
        return text;
    }

    private static String getGenreTitle(int x) {
        switch (x) {
            case Constants.ACTION_NUMBER:
                return Constants.ACTION_STRING;
            case Constants.ADVENTURE_NUMBER:
                return Constants.ADVENTURE_STRING;
            case Constants.ANIMATION_NUMBER:
                return Constants.ANIMATION_STRING;
            case Constants.COMEDY_NUMBER:
                return Constants.COMEDY_STRING;
            case Constants.CRIME_NUMBER:
                return Constants.CRIME_STRING;
            case Constants.DOCUMENTARY_NUMBER:
                return Constants.DOCUMENTARY_STRING;
            case Constants.DRAMA_NUMBER:
                return Constants.DRAMA_STRING;
            case Constants.FAMILY_NUMBER:
                return Constants.FAMILY_STRING;
            case Constants.FANTASY_NUMBER:
                return Constants.FANTASY_STRING;
            case Constants.HISTORY_NUMBER:
                return Constants.HISTORY_STRING;
            case Constants.HORROR_NUMBER:
                return Constants.HORROR_STRING;
            case Constants.MUSIC_NUMBER:
                return Constants.MUSIC_STRING;
            case Constants.MYSTERY_NUMBER:
                return Constants.MYSTERY_STRING;
            case Constants.ROMANCE_NUMBER:
                return Constants.ROMANCE_STRING;
            case Constants.SCIENCE_FICTION_NUMBER:
                return Constants.SCIENCE_FICTION_STRING;
            case Constants.TV_MOVIE_NUMBER:
                return Constants.TV_MOVIE_STRING;
            case Constants.THRILLER_NUMBER:
                return Constants.THRILLER_STRING;
            case Constants.WAR_NUMBER:
                return Constants.WAR_STRING;
            case Constants.WESTERN_NUMBER:
                return Constants.WESTERN_STRING;
        }
        return null;
    }

    public static String convertDate(String date){
        String[] splitDate = date.split("-");
        String year = splitDate[0];
        String month = splitDate[1];
        String day = splitDate[2];
        int mm = Integer.parseInt(month);
        String monthString = getMonth(mm);
        return day+" "+monthString+", "+year;
    }
    private static String getMonth(int month){
        switch (month){
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return String.valueOf(month);
    }
}
