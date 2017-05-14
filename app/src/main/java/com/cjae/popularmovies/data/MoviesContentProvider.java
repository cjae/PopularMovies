package com.cjae.popularmovies.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Jedidiah on 02/05/2017.
 */

@ContentProvider(authority = MoviesContentProvider.AUTHORITY, database = MoviesDatabase.class)
public class MoviesContentProvider {

    static final String AUTHORITY = "com.cjae.popularmovies.data.MoviesContentProvider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String...paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MoviesDatabase.MOVIES)
    public static class Movies {

        private static final String PATH = "movies";

        @ContentUri(
                path = PATH,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MoviesColumns.COLUMN_MOVIE_TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(PATH);

        @InexactContentUri(
                name = MoviesColumns.COLUMN_MOVIE_ID,
                path = PATH + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MoviesColumns.COLUMN_MOVIE_ID,
                pathSegment = 1)
        public static Uri CONTENT_URI_WITH_ID(String id) {
            return buildUri(PATH, id);
        }
    }
}
