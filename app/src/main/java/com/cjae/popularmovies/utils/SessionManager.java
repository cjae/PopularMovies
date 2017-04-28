package com.cjae.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Jedidiah on 18/04/2017.
 */

public class SessionManager {

    private static String SORT_TYPE = "sort_type";

    public static int getSortType(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(SORT_TYPE, 0);
    }

    public static void setSortType(Context context, int sort_type) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SORT_TYPE, sort_type);
        editor.apply();
    }
}
